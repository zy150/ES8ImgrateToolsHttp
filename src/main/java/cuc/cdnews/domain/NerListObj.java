package cuc.cdnews.domain;

import java.util.List;

public class NerListObj {
	private List<HotWord> location;
    private List<HotWord> organization;
    private List<HotWord> person;
    public void setLocation(List<HotWord> location) {
         this.location = location;
     }
     public List<HotWord> getLocation() {
         return location;
     }

    public void setOrganization(List<HotWord> organization) {
         this.organization = organization;
     }
     public List<HotWord> getOrganization() {
         return organization;
     }

    public void setPerson(List<HotWord> person) {
         this.person = person;
     }
     public List<HotWord> getPerson() {
         return person;
     }
}
