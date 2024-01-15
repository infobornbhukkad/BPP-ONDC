// 
// Decompiled by Procyon v0.5.36
// 

package com.bb.beckn.common.model;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ConfigModel implements Serializable
{
	
    private static final long serialVersionUID = -5465633216093570849L;
    private String subscriberId;
    private String subscriberUrl;
    private String becknGateway;
    private String keyid;
    private String disableAdaptorCalls;
    private String whitelistBaps;
    private SigningModel signing;
    private List<ApiParamModel> api;
	private List<DomainsParamModel> domainsList;
    private ApiParamModel matchedApi;
	private DomainsParamModel domain;
    private String contexturl;
	private String becknTtl;
    private String subscriberName;
    private String subscriber_Shortdescription;
    private String subscriber_Longdescription;
    private String groceryCategory;
    private String foodCategory;
    private String logisticDomain;
    private String version;
    private String subscriberlogisticId;
    private String subscriberlogisticUrl;
    private String settlementCounterparty;
    private String settlementPhase;
    private String settlementType;
    private String beneficiaryName;
    private String upiAddress;
    private String servicableradiusProvider;
    private String settlementbankaccNo;
    private String settlementifscCode;
    private String bankName;
    private String branchName;
    private String kiranareturnWindow;
    private String restaurantreturnWindow;    
    private String kiranaserviceRadius;
    private String restaurantserviceRadius;
    private String tracking;
    private String rateable;
    private String cancellable;
    private String returnable;
    
    private String billingsellerName;
    private String billingselleraddressbuildingName;
    private String billingselleraddressbuildingBuilding;
    private String billingselleraddressbuildingLocality;
    private String billingselleraddressbuildingCity;
    private String billingselleraddressbuildingState;
    private String billingselleraddressbuildingCountry;
    private String billingselleraddressbuildingAreacode;
    private String billingsellertaxNumber;
    private String billingsellerPhonenumber;
    private String gstBapfinderfee;
    private String gstItem;
    private String gstDeliverycharge;
    private String waitTime;
    
    
   	public String getWaitTime() {
   		return waitTime;
   	}

   	public void setWaitTime(String waitTime) {
   		this.waitTime = waitTime;
   	}
    
	public String getGstBapfinderfee() {
		return gstBapfinderfee;
	}


	public void setGstBapfinderfee(String gstBapfinderfee) {
		this.gstBapfinderfee = gstBapfinderfee;
	}


	public String getGstItem() {
		return gstItem;
	}


	public void setGstItem(String gstItem) {
		this.gstItem = gstItem;
	}


	public String getGstDeliverycharge() {
		return gstDeliverycharge;
	}


	public void setGstDeliverycharge(String gstDeliverycharge) {
		this.gstDeliverycharge = gstDeliverycharge;
	}


	public String getBillingsellerName() {
		return billingsellerName;
	}


	public void setBillingsellerName(String billingsellerName) {
		this.billingsellerName = billingsellerName;
	}


	public String getBillingselleraddressbuildingName() {
		return billingselleraddressbuildingName;
	}


	public void setBillingselleraddressbuildingName(String billingselleraddressbuildingName) {
		this.billingselleraddressbuildingName = billingselleraddressbuildingName;
	}


	public String getBillingselleraddressbuildingBuilding() {
		return billingselleraddressbuildingBuilding;
	}


	public void setBillingselleraddressbuildingBuilding(String billingselleraddressbuildingBuilding) {
		this.billingselleraddressbuildingBuilding = billingselleraddressbuildingBuilding;
	}


	public String getBillingselleraddressbuildingLocality() {
		return billingselleraddressbuildingLocality;
	}


	public void setBillingselleraddressbuildingLocality(String billingselleraddressbuildingLocality) {
		this.billingselleraddressbuildingLocality = billingselleraddressbuildingLocality;
	}


	public String getBillingselleraddressbuildingCity() {
		return billingselleraddressbuildingCity;
	}


	public void setBillingselleraddressbuildingCity(String billingselleraddressbuildingCity) {
		this.billingselleraddressbuildingCity = billingselleraddressbuildingCity;
	}


	public String getBillingselleraddressbuildingState() {
		return billingselleraddressbuildingState;
	}


	public void setBillingselleraddressbuildingState(String billingselleraddressbuildingState) {
		this.billingselleraddressbuildingState = billingselleraddressbuildingState;
	}


	public String getBillingselleraddressbuildingCountry() {
		return billingselleraddressbuildingCountry;
	}


	public void setBillingselleraddressbuildingCountry(String billingselleraddressbuildingCountry) {
		this.billingselleraddressbuildingCountry = billingselleraddressbuildingCountry;
	}


	public String getBillingselleraddressbuildingAreacode() {
		return billingselleraddressbuildingAreacode;
	}


	public void setBillingselleraddressbuildingAreacode(String billingselleraddressbuildingAreacode) {
		this.billingselleraddressbuildingAreacode = billingselleraddressbuildingAreacode;
	}


	public String getBillingsellertaxNumber() {
		return billingsellertaxNumber;
	}


	public void setBillingsellertaxNumber(String billingsellertaxNumber) {
		this.billingsellertaxNumber = billingsellertaxNumber;
	}


	public String getBillingsellerPhonenumber() {
		return billingsellerPhonenumber;
	}


	public void setBillingsellerPhonenumber(String billingsellerPhonenumber) {
		this.billingsellerPhonenumber = billingsellerPhonenumber;
	}


	public String getCancellable() {
		return cancellable;
	}


	public void setCancellable(String cancellable) {
		this.cancellable = cancellable;
	}


	public String getReturnable() {
		return returnable;
	}


	public void setReturnable(String returnable) {
		this.returnable = returnable;
	}


	public String getRateable() {
		return rateable;
	}


	public void setRateable(String rateable) {
		this.rateable = rateable;
	}


	public String getTracking() {
		return tracking;
	}


	public void setTracking(String tracking) {
		this.tracking = tracking;
	}


	public String getSettlementbankaccNo() {
		return settlementbankaccNo;
	}


	public void setSettlementbankaccNo(String settlementbankaccNo) {
		this.settlementbankaccNo = settlementbankaccNo;
	}


	public String getSettlementifscCode() {
		return settlementifscCode;
	}


	public void setSettlementifscCode(String settlementifscCode) {
		this.settlementifscCode = settlementifscCode;
	}


	public String getBankName() {
		return bankName;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public String getBranchName() {
		return branchName;
	}


	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}


	public String getKiranareturnWindow() {
		return kiranareturnWindow;
	}


	public void setKiranareturnWindow(String kiranareturnWindow) {
		this.kiranareturnWindow = kiranareturnWindow;
	}


	public String getRestaurantreturnWindow() {
		return restaurantreturnWindow;
	}


	public void setRestaurantreturnWindow(String restaurantreturnWindow) {
		this.restaurantreturnWindow = restaurantreturnWindow;
	}


	public String getKiranaserviceRadius() {
		return kiranaserviceRadius;
	}


	public void setKiranaserviceRadius(String kiranaserviceRadius) {
		this.kiranaserviceRadius = kiranaserviceRadius;
	}


	public String getRestaurantserviceRadius() {
		return restaurantserviceRadius;
	}


	public void setRestaurantserviceRadius(String restaurantserviceRadius) {
		this.restaurantserviceRadius = restaurantserviceRadius;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	public String getSettlementCounterparty() {
		return settlementCounterparty;
	}

	public String getServicableradiusProvider() {
		return servicableradiusProvider;
	}

	public void setServicableradiusProvider(String servicableradiusProvider) {
		this.servicableradiusProvider = servicableradiusProvider;
	}

	public void setSettlementCounterparty(String settlementCounterparty) {
		this.settlementCounterparty = settlementCounterparty;
	}

	public String getSettlementPhase() {
		return settlementPhase;
	}

	public void setSettlementPhase(String settlementPhase) {
		this.settlementPhase = settlementPhase;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getUpiAddress() {
		return upiAddress;
	}

	public void setUpiAddress(String upiAddress) {
		this.upiAddress = upiAddress;
	}

	public String getSubscriberlogisticId() {
		return subscriberlogisticId;
	}

	public void setSubscriberlogisticId(String subscriberlogisticId) {
		this.subscriberlogisticId = subscriberlogisticId;
	}

	public String getSubscriberlogisticUrl() {
		return subscriberlogisticUrl;
	}

	public void setSubscriberlogisticUrl(String subscriberlogisticUrl) {
		this.subscriberlogisticUrl = subscriberlogisticUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLogisticDomain() {
		return logisticDomain;
	}

	public void setLogisticDomain(String logisticDomain) {
		this.logisticDomain = logisticDomain;
	}

	public String getGroceryCategory() {
		return groceryCategory;
	}

	public void setGroceryCategory(String groceryCategory) {
		this.groceryCategory = groceryCategory;
	}

	public String getFoodCategory() {
		return foodCategory;
	}

	public void setFoodCategory(String foodCategory) {
		this.foodCategory = foodCategory;
	}

	public String getContexturl() {
		return contexturl;
	}

	public void setContexturl(String contexturl) {
		this.contexturl = contexturl;
	}

	public String getSubscriber_Shortdescription() {
		return subscriber_Shortdescription;
	}

	public void setSubscriber_Shortdescription(String subscriber_Shortdescription) {
		this.subscriber_Shortdescription = subscriber_Shortdescription;
	}

	public String getSubscriber_Longdescription() {
		return subscriber_Longdescription;
	}

	public void setSubscriber_Longdescription(String subscriber_Longdescription) {
		this.subscriber_Longdescription = subscriber_Longdescription;
	}
	
	public String getSubscriberName() {
		return subscriberName;
	}

	public void setSubscriberName(String subscriberName) {
		this.subscriberName = subscriberName;
	}

	public String getBecknTtl() {
		return becknTtl;
	}

	public void setBecknTtl(String becknTtl) {
		this.becknTtl = becknTtl;
	}

	public ConfigModel() {
        this.signing = new SigningModel();
        this.api = new ArrayList<ApiParamModel>();
    }

    public String getSubscriberId() {
        return this.subscriberId;
    }
    
    public String getKeyid() {
        return this.keyid;
    }
    
    public SigningModel getSigning() {
        return this.signing;
    }
    
    public List<ApiParamModel> getApi() {
        return this.api;
    }
    
    public ApiParamModel getMatchedApi() {
        return this.matchedApi;
    }

	public List<DomainsParamModel> getDomainsList() {
        return this.domainsList;
    }

	public DomainsParamModel getDomain() {
        return this.domain;
    }
    
    public void setSubscriberId(final String subscriberId) {
        this.subscriberId = subscriberId;
    }
    
    public void setKeyid(final String keyid) {
        this.keyid = keyid;
    }
    
    public void setSigning(final SigningModel signing) {
        this.signing = signing;
    }
    
    public void setApi(final List<ApiParamModel> api) {
        this.api = api;
    }
    
    public void setMatchedApi(final ApiParamModel matchedApi) {
        this.matchedApi = matchedApi;
    }

	public void setDomainsList(final List<DomainsParamModel> domainsList) {
        this.domainsList = domainsList;
    }
    
    public void setDomain(final DomainsParamModel domain) {
        this.domain = domain;
    }
    
    public String getSubscriberUrl() {
		return subscriberUrl;
	}

	public void setSubscriberUrl(String subscriberUrl) {
		this.subscriberUrl = subscriberUrl;
	}

	public String getBecknGateway() {
		return becknGateway;
	}

	public void setBecknGateway(String becknGateway) {
		this.becknGateway = becknGateway;
	}

	public String getDisableAdaptorCalls() {
		return disableAdaptorCalls;
	}

	public void setDisableAdaptorCalls(String disableAdaptorCalls) {
		this.disableAdaptorCalls = disableAdaptorCalls;
	}

	public String getWhitelistBaps() {
		return whitelistBaps;
	}

	public void setWhitelistBaps(String whitelistBaps) {
		this.whitelistBaps = whitelistBaps;
	}

	@Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConfigModel)) {
            return false;
        }
        final ConfigModel other = (ConfigModel)o;
        if (!other.canEqual(this)) {
            return false;
        }
        final Object this$subscriberId = this.getSubscriberId();
        final Object other$subscriberId = other.getSubscriberId();
        Label_0065: {
            if (this$subscriberId == null) {
                if (other$subscriberId == null) {
                    break Label_0065;
                }
            }
            else if (this$subscriberId.equals(other$subscriberId)) {
                break Label_0065;
            }
            return false;
        }
        final Object this$keyid = this.getKeyid();
        final Object other$keyid = other.getKeyid();
        Label_0102: {
            if (this$keyid == null) {
                if (other$keyid == null) {
                    break Label_0102;
                }
            }
            else if (this$keyid.equals(other$keyid)) {
                break Label_0102;
            }
            return false;
        }
        final Object this$signing = this.getSigning();
        final Object other$signing = other.getSigning();
        Label_0139: {
            if (this$signing == null) {
                if (other$signing == null) {
                    break Label_0139;
                }
            }
            else if (this$signing.equals(other$signing)) {
                break Label_0139;
            }
            return false;
        }
        final Object this$api = this.getApi();
        final Object other$api = other.getApi();
        Label_0176: {
            if (this$api == null) {
                if (other$api == null) {
                    break Label_0176;
                }
            }
            else if (this$api.equals(other$api)) {
                break Label_0176;
            }
            return false;
        }
        final Object this$matchedApi = this.getMatchedApi();
        final Object other$matchedApi = other.getMatchedApi();
        if (this$matchedApi == null) {
            if (other$matchedApi == null) {
                return true;
            }
        }
        else if (this$matchedApi.equals(other$matchedApi)) {
            return true;
        }
        return false;
    }
    
    protected boolean canEqual(final Object other) {
        return other instanceof ConfigModel;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $subscriberId = this.getSubscriberId();
        result = result * 59 + (($subscriberId == null) ? 43 : $subscriberId.hashCode());
        final Object $keyid = this.getKeyid();
        result = result * 59 + (($keyid == null) ? 43 : $keyid.hashCode());
        final Object $signing = this.getSigning();
        result = result * 59 + (($signing == null) ? 43 : $signing.hashCode());
        final Object $api = this.getApi();
        result = result * 59 + (($api == null) ? 43 : $api.hashCode());
        final Object $matchedApi = this.getMatchedApi();
        result = result * 59 + (($matchedApi == null) ? 43 : $matchedApi.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        return "ConfigModel(subscriberId=" + this.getSubscriberId() + ", keyid=" + this.getKeyid() + ", signing=" + this.getSigning() + ", api=" + this.getApi() + ", domainsList=" + this.getDomainsList() + ", matchedApi=" + this.getMatchedApi() + ")";
    }
}
