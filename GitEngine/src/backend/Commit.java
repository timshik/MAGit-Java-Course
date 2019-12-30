package backend;

import org.apache.commons.codec.digest.DigestUtils;
import puk.team.course.magit.ancestor.finder.AncestorFinder;
import puk.team.course.magit.ancestor.finder.CommitRepresentative;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.function.Function;

public class Commit implements CommitRepresentative {
    private String name;
    private String currentShawan;
    private String prevShawan1;
    private String commitMessage;
    private Date commitDate;
    private String CommitMaker;
    private String prevShawan2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit(String currentShawn, String prevShawan1, String commitMessage, Date commitDate, String commitMaker,String prevShawan2) {
        this.currentShawan = currentShawn;
        this.prevShawan1 = prevShawan1;
        this.commitMessage = commitMessage;
        this.commitDate = commitDate;
        CommitMaker = commitMaker;
        this.prevShawan2 = prevShawan2;
        if(prevShawan2.equals("") &prevShawan1.equals(""))
        {
            name = DigestUtils.sha1Hex(StaticMethods.convertToString(this.currentShawan,"Null",this.commitMessage,"Null"));
        }
        else if(prevShawan2.equals(""))
        {
            name = DigestUtils.sha1Hex(StaticMethods.convertToString(this.currentShawan,this.prevShawan1,this.commitMessage,"Null"));
        }
         else if(prevShawan1.equals(""))
        {
            name = DigestUtils.sha1Hex(StaticMethods.convertToString(this.currentShawan,"Null",this.commitMessage,prevShawan2));
        }

        else
        {
            name = DigestUtils.sha1Hex(StaticMethods.convertToString(this.currentShawan,this.prevShawan1,this.commitMessage,this.prevShawan2));
        }

    }

    public String getCurrentShawan() {
        return currentShawan;
    }

    public String getPrevShawan1() {
        return prevShawan1;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public String getCommitMaker() {
        return CommitMaker;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public void setCommitMaker(String commitMaker) {
        CommitMaker = commitMaker;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        return name.equals(((Commit)obj).getName());
    }


    @Override
    public String getSha1() {
        return name;
    }

    @Override
    public String getFirstPrecedingSha1() {
        return prevShawan1;
    }

    @Override
    public String getSecondPrecedingSha1() {
        return prevShawan2;
    }


    }

