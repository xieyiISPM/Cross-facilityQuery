package parties;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Stack;

public class CloudHospital {
    @Getter
    @Setter
    private BigInteger[] genomicSequence; //genomicSequence setter should accept array

    @Getter
    @Setter
    private BigInteger[] query;

    @Getter
    private Pair<BigInteger, BigInteger>[]  indexDistPairArray;
    private Stack<Pair<BigInteger, BigInteger>> indexDistPairStack  = new Stack();

    private ArrayList<Triple<Integer, Integer, Boolean>> indexOrderIsWinList = new ArrayList<>();

    @Getter
    private ArrayList<Triple<BigInteger, Integer, Integer>> finalResultList = new ArrayList<>();


    @Getter
    @Setter
    private int hospitalId;

    public CloudHospital(){

    }

    public CloudHospital(int hospitalId){
        this.hospitalId = hospitalId;
    }

    public void addTopKIndexDistancePair(Pair<BigInteger, BigInteger>[] indexDistPairArray){
        this.indexDistPairArray = indexDistPairArray;
        for(int i = indexDistPairArray.length-1; i >=0; i--){
            indexDistPairStack.push(indexDistPairArray[i]);
        }
    }

    public Pair<BigInteger, BigInteger> popIndexDistPair(){
        return indexDistPairStack.pop();
    }

    public Pair<BigInteger, BigInteger> peekIndexDistPair(){
        return indexDistPairStack.peek();
    }

    public int getGenomicSequenceLength(){
        return genomicSequence.length;
    }

    public int getIndexDistPairArrayLength(){
        return indexDistPairArray.length;
    }

    public int getIndexDistPairStackSize(){
        return indexDistPairStack.size();
    }

    public void genFinalResults(){
        if(indexOrderIsWinList != null){
            for(Triple<Integer, Integer, Boolean> element: indexOrderIsWinList){
                if(element.getRight()==true){
                    finalResultList.add(new ImmutableTriple<>(indexDistPairArray[element.getLeft()].getLeft(), hospitalId, element.getLeft()));
                }
            }
        }
    }

    public void setWinInfoList(Integer index, Integer order, Boolean isWin){
        Triple<Integer, Integer, Boolean> winInfoTriple = new ImmutableTriple<>(index, order, isWin);
        indexOrderIsWinList.add(winInfoTriple);
    }

    public Pair[] getAllIndexDistPairFromStack(){
        Pair[] temp = new Pair[getIndexDistPairStackSize()];
        int i = 0;

        for(Pair<BigInteger, BigInteger> indexDistPair: indexDistPairStack){
            temp[i] = indexDistPair;
            i++;
        }
        return temp;
    }

}
