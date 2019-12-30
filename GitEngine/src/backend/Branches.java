package backend;

import java.io.File;
import java.util.HashSet;
import java.util.Set;


public class Branches {

   private Set< Branch> Branchset = new HashSet<>();

    public Branches(Set< Branch> branchset) {
        Branchset = branchset;
    }

    public Branches() {
    }
    public void Add( Branch branch)
    {
        Branchset.add(branch);
    }

    public Set<Branch> getBranchset() {
        return Branchset;
    }
    public  void BuildBranches(String Path) throws Exception {
        StaticMethods.BuildDir(Path);
        StaticMethods.BuildFile(Path + File.separator + "Head", "Master");
        StaticMethods.BuildFile(Path + File.separator + "Master", "Null");
    }
}
