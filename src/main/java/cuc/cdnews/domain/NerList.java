package cuc.cdnews.domain;

import java.util.List;

public class NerList {
	private List<String> location;
    private List<String> organization;
    private List<String> person;
    public void setLocation(List<String> location) {
         this.location = location;
     }
     public List<String> getLocation() {
         return location;
     }

    public void setOrganization(List<String> organization) {
         this.organization = organization;
     }
     public List<String> getOrganization() {
         return organization;
     }

    public void setPerson(List<String> person) {
         this.person = person;
     }
     public List<String> getPerson() {
         return person;
     }
}
