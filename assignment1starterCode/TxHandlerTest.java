import static org.junit.Assert.*;

/**
 * Created by fuyangliu on 11/28/16.
 */
public class TxHandlerTest {

    @org.junit.Test
    public void testIsValidTx() throws Exception {
        TxHandler handler = new TxHandler(new UTXOPool());

        assertTrue(handler.isValidTx(new Transaction()));
    }
}
