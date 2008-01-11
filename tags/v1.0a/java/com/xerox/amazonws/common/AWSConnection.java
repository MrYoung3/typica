//
// typica - A client library for Amazon Web Services
// Copyright (C) 2007 Xerox Corporation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.xerox.amazonws.common;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import ch.inventec.Base64Coder;

/**
 * This class provides common code to the query and rest connection classes
 *
 * @author D. Kavanagh
 * @author developer@dotech.com
 */
public abstract class AWSConnection {
    private String awsAccessKeyId;
    private String awsSecretAccessKey;
    private boolean isSecure;
    private String server;
    private int port;
	protected Map <String, List<String>> headers;

    /**
	 * Initializes the queue service with your AWS login information.
	 *
     * @param awsAccessId The your user key into AWS
     * @param awsSecretKey The secret string used to generate signatures for authentication.
     * @param isSecure True if the data should be encrypted on the wire on the way to or from SQS.
     * @param server Which host to connect to.  Usually, this will be s3.amazonaws.com
     * @param port Which port to use.
     */
    public AWSConnection(String awsAccessKeyId, String awsSecretAccessKey, boolean isSecure,
                             String server, int port)
    {
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretAccessKey = awsSecretAccessKey;
        this.isSecure = isSecure;
        this.server = server;
        this.port = port;
		this.headers = new TreeMap<String, List<String>>();
    }

	/**
	 * This method provides the URL for the queue service based on initialization.
	 *
	 * @return generated queue service url
	 */
	public URL getUrl() {
		try {
			return makeURL("");
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	protected String getAwsAccessKeyId() {
		return this.awsAccessKeyId;
	}

	protected String getSecretAccessKey() {
		return this.awsSecretAccessKey;
	}

	protected boolean isSecure() {
		return this.isSecure;
	}

	protected String getServer() {
		return this.server;
	}

	protected int getPort() {
		return this.port;
	}

    /**
     * Create a new URL object for a given resource.
     * @param resource The resource name (bucketName + "/" + key).
     */
    protected URL makeURL(String resource) throws MalformedURLException {
        String protocol = this.isSecure ? "https" : "http";
        return new URL(protocol, this.server, this.port, "/"+resource);
    }

    /**
     * Calculate the HMAC/SHA1 on a string.
     * @param data Data to sign
     * @param passcode Passcode to sign it with
     * @return Signature
     * @throws NoSuchAlgorithmException If the algorithm does not exist.  Unlikely
     * @throws InvalidKeyException If the key is invalid.
     */
    protected String encode(String awsSecretAccessKey, String canonicalString,
                                boolean urlencode)
    {
        // The following HMAC/SHA1 code for the signature is taken from the
        // AWS Platform's implementation of RFC2104 (amazon.webservices.common.Signature)
        //
        // Acquire an HMAC/SHA1 from the raw key bytes.
        SecretKeySpec signingKey =
            new SecretKeySpec(awsSecretAccessKey.getBytes(), "HmacSHA1");

        // Acquire the MAC instance and initialize with the signing key.
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            // should not happen
            throw new RuntimeException("Could not find sha1 algorithm", e);
        }
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            // also should not happen
            throw new RuntimeException("Could not initialize the MAC algorithm", e);
        }

        // Compute the HMAC on the digest, and set it.
        String b64 = new String(Base64Coder.encode(mac.doFinal(canonicalString.getBytes())));

        if (urlencode) {
            return urlencode(b64);
        } else {
            return b64;
        }
    }

    protected String urlencode(String unencoded) {
        try {
            return URLEncoder.encode(unencoded, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // should never happen
            throw new RuntimeException("Could not url encode to UTF-8", e);
        }
    }
}
