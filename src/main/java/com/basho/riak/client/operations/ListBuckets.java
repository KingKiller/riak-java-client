/*
 * Copyright 2013 Basho Technologies Inc
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
package com.basho.riak.client.operations;

import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.operations.ListBucketsOperation;
import com.basho.riak.client.util.ByteArrayWrapper;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ListBuckets extends RiakCommand<ListBuckets.Response>
{
	private static final String DEFAULT_BUCKET_TYPE = "default";

    private final int timeout;
    private final String type;

    ListBuckets(Builder builder)
    {
		this.timeout = builder.timeout;
	    this.type = builder.type;
    }

    @Override
    Response execute(RiakCluster cluster) throws ExecutionException, InterruptedException
    {
        ListBucketsOperation.Builder builder = new ListBucketsOperation.Builder();
        if (timeout > 0)
        {
            builder.withTimeout(timeout);
        }
        builder.withBucketType(ByteArrayWrapper.create(type));
        ListBucketsOperation operation = builder.build();
        cluster.execute(operation);
        return new Response(type, operation.get());
    }

    public static class Response implements Iterable<Location> {

        private final String type;
        private final List<ByteArrayWrapper> buckets;

        public Response(String type, List<ByteArrayWrapper> buckets)
        {
            this.type = type;
            this.buckets = buckets;
        }

        @Override
        public Iterator<Location> iterator()
        {
            return new Itr(buckets.iterator(), type);
        }
    }

    private static class Itr implements Iterator<Location>
    {
        private final Iterator<ByteArrayWrapper> iterator;
        private final String type;

        private Itr(Iterator<ByteArrayWrapper> iterator, String type)
        {
            this.iterator = iterator;
            this.type = type;
        }

        @Override
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        @Override
        public Location next()
        {
            ByteArrayWrapper bucket = iterator.next();
            return new Location(type).withType(bucket.toStringUtf8());
        }

        @Override
        public void remove()
        {
            iterator.remove();
        }
    }

	public static class Builder
	{
		private int timeout;
		private final String type;

		public Builder(String type)
		{
			this.type = type;
		}

		public Builder withTimeout(int timeout)
		{
			this.timeout = timeout;
			return this;
		}

		public ListBuckets build()
		{
			return new ListBuckets(this);
		}
	}

}
