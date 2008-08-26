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

package com.xerox.amazonws.sdb;

import com.xerox.amazonws.common.AWSException;

/**
 * A wrapper exception to simplify catching errors related to sdb activity.
 *
 * @author D. Kavanagh
 * @author developer@dotech.com
 */
public class SDBException extends AWSException {

    public SDBException(String s) {
        super(s);
    }

    public SDBException(String s, Exception ex) {
        super(s, ex);
    }

    public SDBException(AWSException ex) {
        super(ex);
    }
}
