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
package com.basho.riak.client.core.operations.itest;

import com.basho.riak.client.cap.Quorum;
import com.basho.riak.client.core.operations.FetchBucketPropsOperation;
import com.basho.riak.client.core.operations.StoreBucketPropsOperation;
import static com.basho.riak.client.core.operations.itest.ITestBase.bucketName;
import static com.basho.riak.client.core.operations.itest.ITestBase.testBucketType;
import com.basho.riak.client.query.BucketProperties;
import com.basho.riak.client.util.BinaryValue;
import java.util.concurrent.ExecutionException;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Test;

/**
 *
 * @author Brian Roach <roach at basho dot com>
 */
public class ITestBucketProperties extends ITestBase
{
    
    @Test
    public void testFetchDefaultBucketProps() throws InterruptedException, ExecutionException
    {
        BucketProperties props = fetchBucketProps(bucketName, null);
        assertTrue(props.hasNVal());
        assertTrue(props.hasAllowMulti());
        assertTrue(props.hasBasicQuorum());
        assertTrue(props.hasBigVClock());
        assertTrue(props.hasChashKeyFunction());
        assertTrue(props.hasLinkwalkFunction());
        assertTrue(props.hasDw());
        assertTrue(props.hasLastWriteWins());
        assertTrue(props.hasLinkwalkFunction());
        assertTrue(props.hasNotFoundOk());
        assertTrue(props.hasOldVClock());
        assertTrue(props.hasPr());
        assertTrue(props.hasPw());
        assertTrue(props.hasR());
        assertTrue(props.hasLegacyRiakSearchEnabled());
        assertTrue(props.hasRw());
        assertTrue(props.hasSmallVClock());
        assertTrue(props.hasW());
        assertTrue(props.hasYoungVClock());
        
        assertFalse(props.hasBackend());
        assertFalse(props.hasPostcommitHooks());
        assertFalse(props.hasPrecommitHooks());
        assertFalse(props.hasSearchIndex());
    }
    
    @Test
    public void testSetDefaultBucketProps() throws InterruptedException, ExecutionException
    {
        StoreBucketPropsOperation.Builder builder = 
            new StoreBucketPropsOperation.Builder(bucketName)
                .withAllowMulti(true)
                .withNVal(4);
        
        storeBucketProps(builder);
        
        BucketProperties props = fetchBucketProps(bucketName, null);
        
        assertEquals(props.getNVal(), Integer.valueOf(4));
        assertTrue(props.getAllowMulti());
        
    }
    
    @Test
    public void testResetBucketProps() throws InterruptedException, ExecutionException
    {
        StoreBucketPropsOperation.Builder builder = 
            new StoreBucketPropsOperation.Builder(bucketName)
                .withNVal(4)
                .withR(1);
        
        storeBucketProps(builder);
        BucketProperties props = fetchBucketProps(bucketName, null);
        
        assertEquals(props.getNVal(), Integer.valueOf(4));
        assertEquals(props.getR().getIntValue(), 1);
        
        resetAndEmptyBucket(bucketName);
        
        props = fetchBucketProps(bucketName, null);
        assertEquals(props.getNVal(), Integer.valueOf(3));
        assertEquals(props.getR(), Quorum.quorumQuorum());
        
    }
    
    @Test
    public void testFetchBucketPropsFromType() throws InterruptedException, ExecutionException
    {
        Assume.assumeTrue(testBucketType);
        BucketProperties props = fetchBucketProps(bucketName, bucketType);
        assertTrue(props.hasNVal());
        assertTrue(props.hasAllowMulti());
        assertTrue(props.hasBasicQuorum());
        assertTrue(props.hasBigVClock());
        assertTrue(props.hasChashKeyFunction());
        assertTrue(props.hasDw());
        assertTrue(props.hasLastWriteWins());
        assertTrue(props.hasNotFoundOk());
        assertTrue(props.hasOldVClock());
        assertTrue(props.hasPr());
        assertTrue(props.hasPw());
        assertTrue(props.hasR());
        assertTrue(props.hasLegacyRiakSearchEnabled());
        assertTrue(props.hasRw());
        assertTrue(props.hasSmallVClock());
        assertTrue(props.hasW());
        assertTrue(props.hasYoungVClock());
        
        assertFalse(props.hasBackend());
        assertFalse(props.hasPostcommitHooks());
        assertFalse(props.hasPrecommitHooks());
        assertFalse(props.hasSearchIndex());
    }
    
    @Test
    public void testSetBucketPropsInType() throws InterruptedException, ExecutionException
    {
        Assume.assumeTrue(testBucketType);
        StoreBucketPropsOperation.Builder builder = 
            new StoreBucketPropsOperation.Builder(bucketName)
                .withBucketType(bucketType)
                .withR(1)
                .withNVal(4);
        
        storeBucketProps(builder);
        BucketProperties props = fetchBucketProps(bucketName, bucketType);
        
        assertEquals(props.getNVal(), Integer.valueOf(4));
        assertEquals(props.getR().getIntValue(), 1);
        
        props = fetchBucketProps(bucketName, null);
        assertEquals(props.getNVal(), Integer.valueOf(3));
    }
    
    private BucketProperties fetchBucketProps(BinaryValue bucketName, BinaryValue bucketType) throws InterruptedException, ExecutionException
    {
        FetchBucketPropsOperation.Builder builder = new FetchBucketPropsOperation.Builder(bucketName);
        if (bucketType != null)
        {
            builder.withBucketType(bucketType);
        }
        FetchBucketPropsOperation op = builder.build();
        cluster.execute(op);
        return op.get();
    }
    
    private void storeBucketProps(StoreBucketPropsOperation.Builder builder) throws InterruptedException, ExecutionException
    {
        StoreBucketPropsOperation op = 
            builder.build();
        cluster.execute(op);
        
        op.get();
    }
    
}
