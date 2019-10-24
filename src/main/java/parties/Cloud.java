package parties;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class Cloud<T> {
    private List<T> hospitals = new ArrayList<>();

    @Setter
    private Set<Integer> winnerSet = new HashSet<>();

    public void addHospital (T hospital){
        hospitals.add(hospital);
    }

    public int getHospitalNum(){
        return hospitals.size();
    }

    public Hospitals getHospital(int index){
        return (Hospitals)hospitals.get(index);
    }

    public int getWinnerSetSize(){
        return winnerSet.size();
    }



}
