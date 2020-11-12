package com.tenderWatch.Models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lcom62 on 26/7/18.
 */

public class PesapalPaymentRESP {
    @SerializedName("newUser")
    @Expose
    private NewUser newUser;
    @SerializedName("serviceDetails")
    @Expose
    private ServiceDetails serviceDetails;

    public NewUser getNewUser() {
        return newUser;
    }

    public void setNewUser(NewUser newUser) {
        this.newUser = newUser;
    }

    public ServiceDetails getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public class NewUser {

        @SerializedName("isActive")
        @Expose
        private Boolean isActive;
        @SerializedName("deviceId")
        @Expose
        private List<String> deviceId = null;
        @SerializedName("pesapalDetails")
        @Expose
        private List<User.PesapalDetail> pesapalDetails = null;
        @SerializedName("subscribe")
        @Expose
        private String subscribe;
        @SerializedName("isPayment")
        @Expose
        private Boolean isPayment;
        @SerializedName("payment")
        @Expose
        private String payment;
        @SerializedName("androidDeviceId")
        @Expose
        private List<String> androidDeviceId = null;
        @SerializedName("lastTenderUploaded")
        @Expose
        private String lastTenderUploaded;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("lastName")
        @Expose
        private String lastName;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("country")
        @Expose
        private String country;
        @SerializedName("contactNo")
        @Expose
        private String contactNo;
        @SerializedName("occupation")
        @Expose
        private String occupation;
        @SerializedName("aboutMe")
        @Expose
        private String aboutMe;
        @SerializedName("role")
        @Expose
        private String role;
        @SerializedName("createdAt")
        @Expose
        private String createdAt;
        @SerializedName("__v")
        @Expose
        private Integer v;
        @SerializedName("stripeDetails")
        @Expose
        private StripeDetails stripeDetails;
        @SerializedName("invoiceURL")
        @Expose
        private String invoiceURL;
        @SerializedName("profilePhoto")
        @Expose
        private String profilePhoto;

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public List<String> getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(List<String> deviceId) {
            this.deviceId = deviceId;
        }

        public List<User.PesapalDetail> getPesapalDetails() {
            return pesapalDetails;
        }

        public void setPesapalDetails(List<User.PesapalDetail> pesapalDetails) {
            this.pesapalDetails = pesapalDetails;
        }

        public String getSubscribe() {
            return subscribe;
        }

        public void setSubscribe(String subscribe) {
            this.subscribe = subscribe;
        }

        public Boolean getIsPayment() {
            return isPayment;
        }

        public void setIsPayment(Boolean isPayment) {
            this.isPayment = isPayment;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public List<String> getAndroidDeviceId() {
            return androidDeviceId;
        }

        public void setAndroidDeviceId(List<String> androidDeviceId) {
            this.androidDeviceId = androidDeviceId;
        }

        public String getLastTenderUploaded() {
            return lastTenderUploaded;
        }

        public void setLastTenderUploaded(String lastTenderUploaded) {
            this.lastTenderUploaded = lastTenderUploaded;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }

        public String getOccupation() {
            return occupation;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }

        public String getAboutMe() {
            return aboutMe;
        }

        public void setAboutMe(String aboutMe) {
            this.aboutMe = aboutMe;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

        public StripeDetails getStripeDetails() {
            return stripeDetails;
        }

        public void setStripeDetails(StripeDetails stripeDetails) {
            this.stripeDetails = stripeDetails;
        }

        public String getInvoiceURL() {
            return invoiceURL;
        }

        public void setInvoiceURL(String invoiceURL) {
            this.invoiceURL = invoiceURL;
        }

        public String getProfilePhoto() {
            return profilePhoto;
        }

        public void setProfilePhoto(String profilePhoto) {
            this.profilePhoto = profilePhoto;
        }

    }

    public class PesapalDetail {

        @SerializedName("register")
        @Expose
        private Boolean register;
        @SerializedName("selections")
        @Expose
        private JsonObject selections;
        @SerializedName("subscriptionPackage")
        @Expose
        private Integer subscriptionPackage;
        @SerializedName("reference")
        @Expose
        private String reference;
        @SerializedName("payment")
        @Expose
        private String payment;
        @SerializedName("pesapalStatus")
        @Expose
        private String pesapalStatus;
        @SerializedName("amount")
        @Expose
        private Integer amount;

        public Boolean getRegister() {
            return register;
        }

        public void setRegister(Boolean register) {
            this.register = register;
        }

        public JsonObject getSelections() {
            return selections;
        }

        public void setSelections(JsonObject selections) {
            this.selections = selections;
        }

        public Integer getSubscriptionPackage() {
            return subscriptionPackage;
        }

        public void setSubscriptionPackage(Integer subscriptionPackage) {
            this.subscriptionPackage = subscriptionPackage;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public String getPesapalStatus() {
            return pesapalStatus;
        }

        public void setPesapalStatus(String pesapalStatus) {
            this.pesapalStatus = pesapalStatus;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

    }

    public class Selections {

        @SerializedName("59e736e29533c015987953d3")
        @Expose
        private List<String> _59e736e29533c015987953d3 = null;
        @SerializedName("59e735479533c0159879539a")
        @Expose
        private List<String> _59e735479533c0159879539a = null;
        @SerializedName("59e735009533c01598795396")
        @Expose
        private List<String> _59e735009533c01598795396 = null;
        @SerializedName("59e7356a9533c0159879539f")
        @Expose
        private List<String> _59e7356a9533c0159879539f = null;
        @SerializedName("59e735289533c01598795397")
        @Expose
        private List<String> _59e735289533c01598795397 = null;
        @SerializedName("59e735289533c01598795398")
        @Expose
        private String _59e735289533c01598795398;
        @SerializedName("59e735479533c0159879539b")
        @Expose
        private String _59e735479533c0159879539b;
        @SerializedName("59e7356a9533c0159879539e")
        @Expose
        private List<String> _59e7356a9533c0159879539e = null;
        @SerializedName("59e737929533c01598795433")
        @Expose
        private List<String> _59e737929533c01598795433 = null;
        @SerializedName("59e736ab9533c015987953b8")
        @Expose
        private List<String> _59e736ab9533c015987953b8 = null;
        @SerializedName("59e736869533c015987953b4")
        @Expose
        private List<String> _59e736869533c015987953b4 = null;
        @SerializedName("59e736369533c015987953a9")
        @Expose
        private List<String> _59e736369533c015987953a9 = null;
        @SerializedName("59e737e39533c0159879547e")
        @Expose
        private List<String> _59e737e39533c0159879547e = null;
        @SerializedName("59e737e39533c01598795485")
        @Expose
        private List<String> _59e737e39533c01598795485 = null;
        @SerializedName("59e737139533c015987953f2")
        @Expose
        private List<String> _59e737139533c015987953f2 = null;
        @SerializedName("59e736e29533c015987953e1")
        @Expose
        private List<String> _59e736e29533c015987953e1 = null;
        @SerializedName("59e737929533c01598795446")
        @Expose
        private List<String> _59e737929533c01598795446 = null;
        @SerializedName("59e737929533c01598795441")
        @Expose
        private List<String> _59e737929533c01598795441 = null;
        @SerializedName("59e737e39533c0159879546c")
        @Expose
        private List<String> _59e737e39533c0159879546c = null;
        @SerializedName("59e736369533c015987953a1")
        @Expose
        private String _59e736369533c015987953a1;

        public List<String> get59e736e29533c015987953d3() {
            return _59e736e29533c015987953d3;
        }

        public void set59e736e29533c015987953d3(List<String> _59e736e29533c015987953d3) {
            this._59e736e29533c015987953d3 = _59e736e29533c015987953d3;
        }

        public List<String> get59e735479533c0159879539a() {
            return _59e735479533c0159879539a;
        }

        public void set59e735479533c0159879539a(List<String> _59e735479533c0159879539a) {
            this._59e735479533c0159879539a = _59e735479533c0159879539a;
        }

        public List<String> get59e735009533c01598795396() {
            return _59e735009533c01598795396;
        }

        public void set59e735009533c01598795396(List<String> _59e735009533c01598795396) {
            this._59e735009533c01598795396 = _59e735009533c01598795396;
        }

        public List<String> get59e7356a9533c0159879539f() {
            return _59e7356a9533c0159879539f;
        }

        public void set59e7356a9533c0159879539f(List<String> _59e7356a9533c0159879539f) {
            this._59e7356a9533c0159879539f = _59e7356a9533c0159879539f;
        }

        public List<String> get59e735289533c01598795397() {
            return _59e735289533c01598795397;
        }

        public void set59e735289533c01598795397(List<String> _59e735289533c01598795397) {
            this._59e735289533c01598795397 = _59e735289533c01598795397;
        }

        public String get59e735289533c01598795398() {
            return _59e735289533c01598795398;
        }

        public void set59e735289533c01598795398(String _59e735289533c01598795398) {
            this._59e735289533c01598795398 = _59e735289533c01598795398;
        }

        public String get59e735479533c0159879539b() {
            return _59e735479533c0159879539b;
        }

        public void set59e735479533c0159879539b(String _59e735479533c0159879539b) {
            this._59e735479533c0159879539b = _59e735479533c0159879539b;
        }

        public List<String> get59e7356a9533c0159879539e() {
            return _59e7356a9533c0159879539e;
        }

        public void set59e7356a9533c0159879539e(List<String> _59e7356a9533c0159879539e) {
            this._59e7356a9533c0159879539e = _59e7356a9533c0159879539e;
        }

        public List<String> get59e737929533c01598795433() {
            return _59e737929533c01598795433;
        }

        public void set59e737929533c01598795433(List<String> _59e737929533c01598795433) {
            this._59e737929533c01598795433 = _59e737929533c01598795433;
        }

        public List<String> get59e736ab9533c015987953b8() {
            return _59e736ab9533c015987953b8;
        }

        public void set59e736ab9533c015987953b8(List<String> _59e736ab9533c015987953b8) {
            this._59e736ab9533c015987953b8 = _59e736ab9533c015987953b8;
        }

        public List<String> get59e736869533c015987953b4() {
            return _59e736869533c015987953b4;
        }

        public void set59e736869533c015987953b4(List<String> _59e736869533c015987953b4) {
            this._59e736869533c015987953b4 = _59e736869533c015987953b4;
        }

        public List<String> get59e736369533c015987953a9() {
            return _59e736369533c015987953a9;
        }

        public void set59e736369533c015987953a9(List<String> _59e736369533c015987953a9) {
            this._59e736369533c015987953a9 = _59e736369533c015987953a9;
        }

        public List<String> get59e737e39533c0159879547e() {
            return _59e737e39533c0159879547e;
        }

        public void set59e737e39533c0159879547e(List<String> _59e737e39533c0159879547e) {
            this._59e737e39533c0159879547e = _59e737e39533c0159879547e;
        }

        public List<String> get59e737e39533c01598795485() {
            return _59e737e39533c01598795485;
        }

        public void set59e737e39533c01598795485(List<String> _59e737e39533c01598795485) {
            this._59e737e39533c01598795485 = _59e737e39533c01598795485;
        }

        public List<String> get59e737139533c015987953f2() {
            return _59e737139533c015987953f2;
        }

        public void set59e737139533c015987953f2(List<String> _59e737139533c015987953f2) {
            this._59e737139533c015987953f2 = _59e737139533c015987953f2;
        }

        public List<String> get59e736e29533c015987953e1() {
            return _59e736e29533c015987953e1;
        }

        public void set59e736e29533c015987953e1(List<String> _59e736e29533c015987953e1) {
            this._59e736e29533c015987953e1 = _59e736e29533c015987953e1;
        }

        public List<String> get59e737929533c01598795446() {
            return _59e737929533c01598795446;
        }

        public void set59e737929533c01598795446(List<String> _59e737929533c01598795446) {
            this._59e737929533c01598795446 = _59e737929533c01598795446;
        }

        public List<String> get59e737929533c01598795441() {
            return _59e737929533c01598795441;
        }

        public void set59e737929533c01598795441(List<String> _59e737929533c01598795441) {
            this._59e737929533c01598795441 = _59e737929533c01598795441;
        }

        public List<String> get59e737e39533c0159879546c() {
            return _59e737e39533c0159879546c;
        }

        public void set59e737e39533c0159879546c(List<String> _59e737e39533c0159879546c) {
            this._59e737e39533c0159879546c = _59e737e39533c0159879546c;
        }

        public String get59e736369533c015987953a1() {
            return _59e736369533c015987953a1;
        }

        public void set59e736369533c015987953a1(String _59e736369533c015987953a1) {
            this._59e736369533c015987953a1 = _59e736369533c015987953a1;
        }

    }

    public class Selections_ {

        @SerializedName("59e735289533c01598795398")
        @Expose
        private String _59e735289533c01598795398;

        public String get59e735289533c01598795398() {
            return _59e735289533c01598795398;
        }

        public void set59e735289533c01598795398(String _59e735289533c01598795398) {
            this._59e735289533c01598795398 = _59e735289533c01598795398;
        }

    }

    public class ServiceDetails {

        @SerializedName("selections")
        @Expose
        private HashMap<String, ArrayList<String>> selections;
        @SerializedName("subscriptionPackage")
        @Expose
        private Integer subscriptionPackage;
        @SerializedName("register")
        @Expose
        private Boolean register;
        @SerializedName("reference")
        @Expose
        private String reference;
        @SerializedName("payment")
        @Expose
        private String payment;
        @SerializedName("amount")
        @Expose
        private Integer amount;
        @SerializedName("pesapalStatus")
        @Expose
        private String pesapalStatus;

        public HashMap<String, ArrayList<String>> getSelections() {
            return selections;
        }

        public void setSelections(HashMap<String, ArrayList<String>> selections) {
            this.selections = selections;
        }

        public Integer getSubscriptionPackage() {
            return subscriptionPackage;
        }

        public void setSubscriptionPackage(Integer subscriptionPackage) {
            this.subscriptionPackage = subscriptionPackage;
        }

        public Boolean getRegister() {
            return register;
        }

        public void setRegister(Boolean register) {
            this.register = register;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getPayment() {
            return payment;
        }

        public void setPayment(String payment) {
            this.payment = payment;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getPesapalStatus() {
            return pesapalStatus;
        }

        public void setPesapalStatus(String pesapalStatus) {
            this.pesapalStatus = pesapalStatus;
        }

    }

    public class Sources {

        @SerializedName("object")
        @Expose
        private String object;
        @SerializedName("data")
        @Expose
        private List<Object> data = null;
        @SerializedName("has_more")
        @Expose
        private Boolean hasMore;
        @SerializedName("total_count")
        @Expose
        private Integer totalCount;
        @SerializedName("url")
        @Expose
        private String url;

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public List<Object> getData() {
            return data;
        }

        public void setData(List<Object> data) {
            this.data = data;
        }

        public Boolean getHasMore() {
            return hasMore;
        }

        public void setHasMore(Boolean hasMore) {
            this.hasMore = hasMore;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public class StripeDetails {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("object")
        @Expose
        private String object;
        @SerializedName("account_balance")
        @Expose
        private Integer accountBalance;
        @SerializedName("created")
        @Expose
        private Integer created;
        @SerializedName("currency")
        @Expose
        private Object currency;
        @SerializedName("default_source")
        @Expose
        private Object defaultSource;
        @SerializedName("delinquent")
        @Expose
        private Boolean delinquent;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("discount")
        @Expose
        private Object discount;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("invoice_prefix")
        @Expose
        private String invoicePrefix;
        @SerializedName("livemode")
        @Expose
        private Boolean livemode;
        @SerializedName("shipping")
        @Expose
        private Object shipping;
        @SerializedName("sources")
        @Expose
        private Sources sources;
        @SerializedName("subscriptions")
        @Expose
        private Subscriptions subscriptions;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public Integer getAccountBalance() {
            return accountBalance;
        }

        public void setAccountBalance(Integer accountBalance) {
            this.accountBalance = accountBalance;
        }

        public Integer getCreated() {
            return created;
        }

        public void setCreated(Integer created) {
            this.created = created;
        }

        public Object getCurrency() {
            return currency;
        }

        public void setCurrency(Object currency) {
            this.currency = currency;
        }

        public Object getDefaultSource() {
            return defaultSource;
        }

        public void setDefaultSource(Object defaultSource) {
            this.defaultSource = defaultSource;
        }

        public Boolean getDelinquent() {
            return delinquent;
        }

        public void setDelinquent(Boolean delinquent) {
            this.delinquent = delinquent;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getDiscount() {
            return discount;
        }

        public void setDiscount(Object discount) {
            this.discount = discount;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getInvoicePrefix() {
            return invoicePrefix;
        }

        public void setInvoicePrefix(String invoicePrefix) {
            this.invoicePrefix = invoicePrefix;
        }

        public Boolean getLivemode() {
            return livemode;
        }

        public void setLivemode(Boolean livemode) {
            this.livemode = livemode;
        }

        public Object getShipping() {
            return shipping;
        }

        public void setShipping(Object shipping) {
            this.shipping = shipping;
        }

        public Sources getSources() {
            return sources;
        }

        public void setSources(Sources sources) {
            this.sources = sources;
        }

        public Subscriptions getSubscriptions() {
            return subscriptions;
        }

        public void setSubscriptions(Subscriptions subscriptions) {
            this.subscriptions = subscriptions;
        }

    }

    public class Subscriptions {

        @SerializedName("object")
        @Expose
        private String object;
        @SerializedName("data")
        @Expose
        private List<Object> data = null;
        @SerializedName("has_more")
        @Expose
        private Boolean hasMore;
        @SerializedName("total_count")
        @Expose
        private Integer totalCount;
        @SerializedName("url")
        @Expose
        private String url;

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public List<Object> getData() {
            return data;
        }

        public void setData(List<Object> data) {
            this.data = data;
        }

        public Boolean getHasMore() {
            return hasMore;
        }

        public void setHasMore(Boolean hasMore) {
            this.hasMore = hasMore;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}
