package com.basho.riak.client;

import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.RiakFutureListener;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.operations.FetchBucketPropsOperation;
import com.basho.riak.client.core.operations.FetchOperation;
import com.basho.riak.client.query.BucketProperties;
import com.basho.riak.client.query.RiakObject;
import com.basho.riak.client.util.BinaryValue;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Technology preview
 * 
 * @author Brian Roach <roach at basho dot com>
 */
public class App implements RiakFutureListener<RiakObject>
{
    private final RiakCluster cluster;
    
    public App() throws Exception
    {
        RiakNode.Builder builder = new RiakNode.Builder()
                                        .withMinConnections(10);
        
        cluster = new RiakCluster.Builder(builder.build()).build();
        cluster.start();
        Thread.sleep(3000);
    }
    
    public void doIt() throws InterruptedException, ExecutionException
    {
        FetchBucketPropsOperation bpOp =
            new FetchBucketPropsOperation.Builder(BinaryValue.unsafeCreate("test_bucket3)".getBytes()))
                .withBucketType(BinaryValue.unsafeCreate("test_type2".getBytes()))
                .build();
                               
        cluster.execute(bpOp);
        BucketProperties props = bpOp.get();
        System.out.println(props);
        
        
        FetchOperation fetchOp =
            new FetchOperation.Builder(BinaryValue.unsafeCreate("test_bucket2".getBytes()), BinaryValue.unsafeCreate("test_key2".getBytes()))
                .build();
                    
        
        //fetchOp.addListener(this);
        cluster.execute(fetchOp);
        FetchOperation.Response resp = fetchOp.get();
        System.out.println(resp.isNotFound());
        for (RiakObject ro : resp.getObjectList())
        {
            System.out.println("value: " + ro.getValue());
            System.out.println(ro.isDeleted());
        }
        cluster.shutdown();
    }
    
    public static void main( String[] args ) throws Exception
    {
        App a = new App();
        a.doIt();
    }

    @Override
    public void handle(RiakFuture<RiakObject> f)
    {
        try
        {
            RiakObject ro = f.get();
            System.out.println("value: " + ro.getValue());
            System.out.println(ro.isDeleted());
            
            cluster.shutdown();
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ExecutionException ex)
        {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
