/*
 * Copyright 2013 Basho Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.basho.riak.client.core.operations;

import com.basho.riak.client.core.FutureOperation;
import com.basho.riak.client.core.RiakMessage;
import com.basho.riak.client.util.ByteArrayWrapper;
import com.basho.riak.client.util.RiakMessageCodes;
import com.basho.riak.protobuf.RiakPB;
import com.google.protobuf.ByteString;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Brian Roach <roach at basho dot com>
 * @since 2.0
 */
public class ResetBucketTypePropsOperation extends FutureOperation<Boolean, Void>
{
    private final RiakPB.RpbResetBucketTypeReq.Builder reqBuilder;
    
    private ResetBucketTypePropsOperation(Builder builder)
    {
        this.reqBuilder = builder.reqBuilder;
    }
    
    @Override
    protected Boolean convert(List<Void> rawResponse) throws ExecutionException
    {
        return true;
    }

    @Override
    protected RiakMessage createChannelMessage()
    {
        RiakPB.RpbResetBucketTypeReq req = reqBuilder.build();
        return new RiakMessage(RiakMessageCodes.MSG_ResetBucketTypeReq, req.toByteArray());
    }

    @Override
    protected Void decode(RiakMessage rawMessage)
    {
        Operations.checkMessageType(rawMessage, RiakMessageCodes.MSG_ResetBucketResp);
        return null;
    }
    
    public static class Builder
    {
        RiakPB.RpbResetBucketTypeReq.Builder reqBuilder = 
            RiakPB.RpbResetBucketTypeReq.newBuilder();
        
        public Builder(ByteArrayWrapper bucketType)
        {
            if (null == bucketType || bucketType.length() == 0)
            {
                throw new IllegalArgumentException("Bucket type cannot be null or zero length.");
            }
            reqBuilder.setType(ByteString.copyFrom(bucketType.unsafeGetValue()));
        }
        
        public ResetBucketTypePropsOperation build()
        {
            return new ResetBucketTypePropsOperation(this);
        }
    }
    
}
