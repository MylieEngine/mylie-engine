package mylie.engine.core.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashTest {
    @Test
    public void testHashEquals(){
        String args="Hello";
        Hash hash=new Hash(AsyncTestData.SELF_LOCKING,args);
        Hash hash2=new Hash(AsyncTestData.SELF_LOCKING,args);
        Assertions.assertEquals(hash,hash2);
    }


    @SuppressWarnings({"AssertBetweenInconvertibleTypes", "SimplifiableAssertion", "EqualsBetweenInconvertibleTypes"})
    @Test
    public void testHashNotEquals(){
        String args="Hello";
        Hash hash=new Hash(AsyncTestData.SELF_LOCKING,args);
        Hash hash2=new Hash(AsyncTestData.SELF_LOCKING,"World");
        Assertions.assertNotEquals(hash,hash2);
        Assertions.assertNotEquals("Hello",hash);
        Assertions.assertFalse(hash.equals("Hello"));
    }

    @Test
    public void testHashToString(){
        String args="Hello";
        Hash hash=new Hash(AsyncTestData.SELF_LOCKING,args);
        Assertions.assertEquals("Hash< " + hash.hashCode() + " >",hash.toString());
    }

    @Test
    public void testCustomHash(){
        CustomHash object=new CustomHash(1);
        CustomHash object2=new CustomHash(1);
        Hash hash=new Hash(AsyncTestData.SELF_LOCKING,object);
        Hash hash2=new Hash(AsyncTestData.SELF_LOCKING,object2);
        Assertions.assertNotEquals(object,object2);
        Assertions.assertEquals(hash,hash2);
    }



    private static class CustomHash implements mylie.engine.core.async.CustomHash{
        final int customHash;
        CustomHash(int code){
            this.customHash=code;
        }

        @Override
        public int customHash() {
            return customHash;
        }
    }
}
