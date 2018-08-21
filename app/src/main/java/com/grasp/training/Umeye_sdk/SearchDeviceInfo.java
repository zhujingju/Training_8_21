package com.grasp.training.Umeye_sdk;

import java.io.Serializable;

public class SearchDeviceInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8562951431755164029L;
	public int dwVendorId;
	public String sDevModel;
	public String sUMDevModel;
	public String sDevName;
	public String sDevId;
	public String sDevUserName;
	public int bIfSetPwd;
	public String sCloudServerAddr;
	public int usCloudServerPort;
	public int usChNum;
	public int iDevIdType;
	public int bIfAllowSetIpaddr;
	public int bIfEnableDhcp;
	public int iAdapterNum;
	public String sAdapterName_1;
	public String sAdapterMac_1;
	public String sIpaddr_1;
	public String sNetmask_1;
	public String sGateway_1;
	public String sAdapterName_2;
	public String sAdapterMac_2;
	public String sIpaddr_2;
	public String sNetmask_2;
	public String sGateway_2;
	public String sAdapterName_3;
	public String sAdapterMac_3;
	public String sIpaddr_3;
	public String sNetmask_3;
	public String sGateway_3;
	public int iDevPort; // 设备的端口号

	public String currentIp; // 当前连接网卡ip
	/**
	 * //设备连接服务器状态定义 #define NPC_D_MPI_MON_DEV_SRV_CONN_STATE_NON_CONNECT 0
	 * //未连接 #define NPC_D_MPI_MON_DEV_SRV_CONN_STATE_CONNECTING 1 //正在连接
	 * #define NPC_D_MPI_MON_DEV_SRV_CONN_STATE_CONNECT_OK 2 //已连接 #define
	 * NPC_D_MPI_MON_DEV_SRV_CONN_STATE_CONNECT_FAIL 3 //连接失败
	 */
	public int connectState; // 当前连接状态
	/**
	 * #define NPC_D_MPI_MON_DEV_SRV_CONN_FAIL_CODE_SUCCESS 0 //成功 #define
	 * NPC_D_MPI_MON_DEV_SRV_CONN_FAIL_CODE_DNS_FAIL 1
	 * //域名解析失败（可能为设备未接入公网，或者域名错误） #define
	 * NPC_D_MPI_MON_DEV_SRV_CONN_FAIL_CODE_CONN_SRV_FAIL 2
	 * //连接服务器失败（可能为服务器未运行，或者防火墙阻止了通讯） #define
	 * NPC_D_MPI_MON_DEV_SRV_CONN_FAIL_CODE_LOGIN_AUTH_FAIL 3
	 * //登录认证失败（可能是UMID未授权，或认证码错误） #define
	 * NPC_D_MPI_MON_DEV_SRV_CONN_FAIL_CODE_UMID_REGISTERED 4
	 * //UMID已经注册（UMID被其它设备注册到服务器） #define
	 * NPC_D_MPI_MON_DEV_SRV_CONN_FAIL_CODE_OTHER_FAIL 99 //其它失败
	 */
	public int iSrvConnResult; // 连接失败后错误

	public String serverState;

	public SearchDeviceInfo(int dwVendorId, String sDevName, String sDevId,
                            String sDevUserName, int bIfSetPwd, int bIfEnableDhcp,
                            String sAdapterName_1, String sAdapterMac_1, String sIpaddr_1,
                            String sNetmask_1, String sGateway_1, int usChNum, int iDevPort,
                            String sDevModel, String currentIp, int connectState,
                            int iSrvConnResult) {
		super();

		this.sDevModel = sDevModel;
		this.usChNum = usChNum;
		this.dwVendorId = dwVendorId;
		this.sDevName = sDevName;
		this.sDevId = sDevId;
		this.sDevUserName = sDevUserName;
		this.bIfSetPwd = bIfSetPwd;
		this.bIfEnableDhcp = bIfEnableDhcp;
		this.sAdapterName_1 = sAdapterName_1;
		this.sAdapterMac_1 = sAdapterMac_1;
		this.sIpaddr_1 = sIpaddr_1;
		this.sNetmask_1 = sNetmask_1;
		this.sGateway_1 = sGateway_1;
		this.iDevPort = iDevPort;
		this.currentIp = currentIp;
		this.connectState = connectState;
		this.iSrvConnResult = iSrvConnResult;
		this.serverState = serverState(connectState, iSrvConnResult);
	}

	public String serverState(int connectState, int iSrvConnResult) {
		String serverstate = "";
		switch (connectState) {
		case 0:
			serverstate="Not Connected";
			break;
		case 1:
			serverstate="Connecting";
			break;
		case 2:
			serverstate="Success";
			break;
		case 3:
			switch (iSrvConnResult) {
	
			case 1:
				serverstate="DNS Fail";
				break;
			case 2:
				serverstate="Connect Fail";
				break;
			case 3:
				serverstate="The ID is illegal";
				break;
			case 4:
				serverstate="The ID is repeat";
				break;
			default:
				serverstate="Other error";
				break;
			}
			break;

		default:

			break;
		}

		return serverstate;
	}

	public int getDwVendorId() {
		return dwVendorId;
	}

	public void setDwVendorId(int dwVendorId) {
		this.dwVendorId = dwVendorId;
	}

	public String getsDevName() {
		return sDevName;
	}

	public void setsDevName(String sDevName) {
		this.sDevName = sDevName;
	}

	public String getsDevId() {
		return sDevId;
	}

	public void setsDevId(String sDevId) {
		this.sDevId = sDevId;
	}

	public String getsDevUserName() {
		return sDevUserName;
	}

	public void setsDevUserName(String sDevUserName) {
		this.sDevUserName = sDevUserName;
	}

	public int getbIfSetPwd() {
		return bIfSetPwd;
	}

	public void setbIfSetPwd(int bIfSetPwd) {
		this.bIfSetPwd = bIfSetPwd;
	}

	public int getbIfEnableDhcp() {
		return bIfEnableDhcp;
	}

	public void setbIfEnableDhcp(int bIfEnableDhcp) {
		this.bIfEnableDhcp = bIfEnableDhcp;
	}

	public String getsAdapterName_1() {
		return sAdapterName_1;
	}

	public void setsAdapterName_1(String sAdapterName_1) {
		this.sAdapterName_1 = sAdapterName_1;
	}

	public String getsAdapterMac_1() {
		return sAdapterMac_1;
	}

	public void setsAdapterMac_1(String sAdapterMac_1) {
		this.sAdapterMac_1 = sAdapterMac_1;
	}

	public String getsIpaddr_1() {
		return sIpaddr_1;
	}

	public void setsIpaddr_1(String sIpaddr_1) {
		this.sIpaddr_1 = sIpaddr_1;
	}

	public String getsNetmask_1() {
		return sNetmask_1;
	}

	public void setsNetmask_1(String sNetmask_1) {
		this.sNetmask_1 = sNetmask_1;
	}

	public String getsGateway_1() {
		return sGateway_1;
	}

	public void setsGateway_1(String sGateway_1) {
		this.sGateway_1 = sGateway_1;
	}

}
