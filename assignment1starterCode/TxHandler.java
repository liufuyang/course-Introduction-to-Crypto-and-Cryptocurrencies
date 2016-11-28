import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TxHandler {

    private UTXOPool utxoPool;

    public TxHandler(UTXOPool utxoPool) {
        this.utxoPool = new UTXOPool(utxoPool);
    }

    public boolean isValidTx(Transaction tx) {
//        boolean isValid = tx.getInputs().stream()
//                .map(input -> new UTXO(input.prevTxHash, input.outputIndex))
//                .allMatch(utxo -> utxoPool.contains(utxo));
//
//        isValid = isValid && tx.getInputs().stream()
//                .allMatch(input -> Crypto.verifySignature(
//                        utxoPool.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).address,
//                        tx.getRawDataToSign(input.outputIndex),
//                        input.signature));
//
//        isValid = isValid && (tx.getInputs().stream().map(input -> new UTXO(input.prevTxHash, input.outputIndex)).distinct().count()
//                == tx.getInputs().stream().count()
//        );
//
//        isValid = isValid && tx.getOutputs().stream().allMatch(output -> output.value >= 0.0);
//
//        isValid = isValid && (tx.getInputs().stream().mapToDouble(input -> utxoPool.getTxOutput(new UTXO(input.prevTxHash, input.outputIndex)).value).sum() >= tx.getOutputs().stream().mapToDouble(output -> output.value).sum());


        //
        boolean isValid = true;
        int len = tx.getInputs().size();

        Set<UTXO> set = new HashSet<>();
        double oldSum = 0.0;
        double txSum = 0.0;

        for(int i = 0; i<len; i++) {
            UTXO utxo = new UTXO(tx.getInput(i).prevTxHash, tx.getInput(i).outputIndex);
            isValid = isValid && utxoPool.contains(utxo);

            isValid = isValid && Crypto.verifySignature(
                    utxoPool.getTxOutput(utxo).address,
                    tx.getRawDataToSign(tx.getInput(i).outputIndex),
                    tx.getInput(i).signature);

            set.add(utxo);

            isValid = isValid && (tx.getOutput(i).value >= 0.0);

            oldSum += utxoPool.getTxOutput(utxo).value;
            txSum += tx.getOutput(i).value;
        }


        return  isValid && (set.size() == len) && (oldSum >= txSum);
    }

    public Transaction[] handleTxs(Transaction[] possibleTxs) {

        ArrayList<Transaction> txs = new ArrayList<>();

        for (int i = 0; i < possibleTxs.length; i++) {
            boolean isValid = isValidTx(possibleTxs[i]);

            if (isValid) {
                txs.add(possibleTxs[i]);

                for(int j=0; j<possibleTxs[i].getInputs().size(); j++) {
                    utxoPool.removeUTXO(new UTXO(possibleTxs[i].getInput(j).prevTxHash, possibleTxs[i].getInput(j).outputIndex));
                    utxoPool.addUTXO(new UTXO(possibleTxs[i].getHash(), j), possibleTxs[i].getOutput(j));
                }
            }
        }

        return txs.toArray(new Transaction[txs.size()]);

//        return Stream.of(possibleTxs).filter(transaction -> {
//            boolean isValid = isValidTx(transaction);
//
//            if (isValid) {
//                IntStream.range(0, transaction.getInputs().size())
//                        .forEach(i -> {
//                            utxoPool.removeUTXO(new UTXO(transaction.getInput(i).prevTxHash, transaction.getInput(i).outputIndex));
//                            utxoPool.addUTXO(new UTXO(transaction.getHash(), i), transaction.getOutput(i));
//                        });
//
//                return true;
//            } else {
//                return false;
//            }
//        }).toArray(Transaction[]::new);

    }
}
