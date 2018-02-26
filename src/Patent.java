import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "patNum", "iDate", "title", "abstract", "inventors", "assignee", "familyID", "applNum",
		"dateFiled", "docID", "pubDate", "USclass", "references", "claims", "examiner", "legalfirm", "summary",
		"description" })
public class Patent {

	@JsonProperty("patNum")
	private String patNum;
	@JsonProperty("iDate")
	private String iDate;
	@JsonProperty("title")
	private String title;
	@JsonProperty("abstract")
	private String _abstract;
	@JsonProperty("inventors")
	private String inventors;
	@JsonProperty("assignee")
	private String assignee;
	@JsonProperty("familyID")
	private String familyID;
	@JsonProperty("applNum")
	private String applNum;
	@JsonProperty("dateFiled")
	private String dateFiled;
	@JsonProperty("docID")
	private String docID;
	@JsonProperty("pubDate")
	private String pubDate;
	@JsonProperty("USclass")
	private String uSclass;
	@JsonProperty("references")
	private References references;
	@JsonProperty("claims")
	private Claims claims;
	@JsonProperty("examiner")
	private String examiner;
	@JsonProperty("legalfirm")
	private String legalfirm;
	@JsonProperty("summary")
	private String summary;
	@JsonProperty("description")
	private String description;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("patNum")
	public String getPatNum() {
		return patNum;
	}

	@JsonProperty("patNum")
	public void setPatNum(String patNum) {
		this.patNum = patNum;
	}

	@JsonProperty("iDate")
	public String getIDate() {
		return iDate;
	}

	@JsonProperty("iDate")
	public void setIDate(String iDate) {
		this.iDate = iDate;
	}

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	@JsonProperty("abstract")
	public String getAbstract() {
		return _abstract;
	}

	@JsonProperty("abstract")
	public void setAbstract(String _abstract) {
		this._abstract = _abstract;
	}

	@JsonProperty("inventors")
	public List<String> getInventors() {
		List<String> list = Arrays.asList(inventors.split("\\), "));
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i) + ")");
		}
		return list;
	}

	@JsonProperty("inventors")
	public void setInventors(String inventors) {
		// make a list of strings
		this.inventors = inventors;
	}

	@JsonProperty("assignee")
	public String getAssignee() {
		return assignee;
	}

	@JsonProperty("assignee")
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	@JsonProperty("familyID")
	public String getFamilyID() {
		return familyID;
	}

	@JsonProperty("familyID")
	public void setFamilyID(String familyID) {
		this.familyID = familyID;
	}

	@JsonProperty("applNum")
	public String getApplNum() {
		return applNum;
	}

	@JsonProperty("applNum")
	public void setApplNum(String applNum) {
		this.applNum = applNum;
	}

	@JsonProperty("dateFiled")
	public String getDateFiled() {
		return dateFiled;
	}

	@JsonProperty("dateFiled")
	public void setDateFiled(String dateFiled) {
		this.dateFiled = dateFiled;
	}

	@JsonProperty("docID")
	public String getDocID() {
		return docID;
	}

	@JsonProperty("docID")
	public void setDocID(String docID) {
		this.docID = docID;
	}

	@JsonProperty("pubDate")
	public String getPubDate() {
		return pubDate;
	}

	@JsonProperty("pubDate")
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	@JsonProperty("USclass")
	public String getUSclass() {
		return uSclass;
	}

	@JsonProperty("USclass")
	public void setUSclass(String uSclass) {
		this.uSclass = uSclass;
	}

	@SuppressWarnings("unchecked")
	@JsonProperty("references")
	public List<String> getReferences() {
		Collection<Object> refvalues = references.getAdditionalProperties().values();
		List<String> reflist = (List<String>) (List<?>) Arrays.asList(refvalues.toArray());
		return reflist;
		// List<Triplet<String,String,String>> reftriplets = new
		// ArrayList<Triplet<String,String,String>>();
		// for (String ref : reflist){
		// String[] resplit = ref.split(";");
		// reftriplets.add(Triplet.with(resplit[0], resplit[1], resplit[2]));
		// }
		// return reftriplets;
	}

	@JsonProperty("references")
	public void setReferences(References references) {
		this.references = references;
	}

	@SuppressWarnings("unchecked")
	@JsonProperty("claims")
	public List<String> getClaims() {
		Collection<Object> claimvalues = claims.getAdditionalProperties().values();
		return (List<String>) (List<?>) Arrays.asList(claimvalues.toArray());
	}

	@JsonProperty("claims")
	public void setClaims(Claims claims) {
		// make claims a list of strings
		this.claims = claims;
	}

	@JsonProperty("examiner")
	public String getExaminer() {
		return examiner;
	}

	@JsonProperty("examiner")
	public void setExaminer(String examiner) {
		this.examiner = examiner;
	}

	@JsonProperty("legalfirm")
	public String getLegalfirm() {
		return legalfirm;
	}

	@JsonProperty("legalfirm")
	public void setLegalfirm(String legalfirm) {
		this.legalfirm = legalfirm;
	}

	@JsonProperty("summary")
	public String getSummary() {
		return summary;
	}

	@JsonProperty("summary")
	public void setSummary(String summary) {
		this.summary = summary;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
