package org.areasy.runtime.engine.base;

/*
 * Copyright (c) 2007-2016 AREasy Runtime
 *
 * This library, AREasy Runtime and API for BMC Remedy AR System, is free software ("Licensed Software");
 * you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * including but not limited to, the implied warranty of MERCHANTABILITY, NONINFRINGEMENT,
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */

/**
 * This class will gather all field ids used by all workflows.
 *
 */
public interface ARDictionary
{
	public static final int[] 	 CONST_PEOPLE_ROLEIDS 					= { 76000, 31000, 39000, 8000, 10000, 6000 };
	public static final String[] CONST_PEOPLE_ROLENAMES 				= { "Approved by", "Created by", "Managed by", "Owned by", "Supported by", "Used by" };
	public static final String[] CONST_PEOPLE_RELATIONENTITIES 			= { "People", "Support Group" };

	public static final int CI_ENTRYID 									= 1;
	public static final int CI_STATUS									= 7;
	public static final int CI_INSTANCEID 								= 179;
	public static final int CI_NAME 									= 200000020;
	public static final int CI_CLASSID 									= 400079600;
	public static final int CI_DATASETID 								= 400127400;
	public static final int CI_CATEGORY 								= 200000003;
	public static final int CI_TYPE 									= 200000004;
	public static final int CI_ITEM 									= 200000005;
	public static final int CI_MODEL 									= 240001002;
	public static final int CI_MANUFACTURER 							= 240001003;
	public static final int CI_SUPPLIER									= 240001008;
	public static final int CI_SERIALNUMBER 							= 200000001;
	public static final int CI_ASSETID 									= 210000000;
	public static final int CI_RECONCILIATIONID 						= 400129200;
	public static final int CI_COMPANYNAME 								= 1000000001;
	public static final int CI_INVFLAG 									= 301241500;
	public static final int CI_INVNAME 									= 301823800;
	public static final int CI_INVKEYWORD 								= 301131900;
	public static final int CI_INVINSTANCEID 							= 301131800;
	public static final int CI_INVRECONCILIATIONID 						= 301771200;
	public static final int CI_SITENAME 								= 260000001;
	public static final int CI_REGIONNAME 								= 200000012;
	public static final int CI_LOCATION 								= 260142109;
	public static final int CI_TAGNUMBER 								= 260100004;
	public static final int CI_PARTNUMBER 								= 200000013;

	public static final int CRL_DATASETID								= 400127400;
	public static final int CRL_NAME									= 200000020;
	public static final int CRL_DEST_RECONCILIATIONID 					= 400131000;
	public static final int CRL_DEST_INSTANCEID 						= 490009000;
	public static final int CRL_DEST_DATASETID 							= 400128900;
	public static final int CRL_DEST_CLSID 								= 490009100;
	public static final int CRL_SRC_RECONCILIATIONID 					= 400130900;
	public static final int CRL_SRC_INSTANCEID 							= 490008000;
	public static final int CRL_SRC_DATASETID 							= 400128800;
	public static final int CRL_SRC_CLSID 								= 490008100;
	public static final int CRL_TYPE									= 400079600;
	public static final int CRL_HASIMPACT								= 530048100;
	public static final int CRL_IMPACTDIR								= 530048000;

	public static final int PCT_CATEGORY 								= 200000003;
	public static final int PCT_TYPE 									= 200000004;
	public static final int PCT_ITEM 									= 200000005;
	public static final int PCT_MODEL 									= 240001002;
	public static final int PCT_MANUFACTURER 							= 240001003;
	public static final int PCT_CLASSKEYWORD 							= 230000009;
	public static final int PCT_COMPANYNAME 							= 1000000720;
	public static final int PCT_SETINCOMPANY 							= 302230600;
	public static final int PCT_ORIGIN									= 301307500;
	public static final int PCT_STATUS									= 7;
	public static final int PCT_SUITEDEF								= 301312800;

	public static final int CFG_CATEGORY 								= 1000000063;
	public static final int CFG_TYPE 									= 1000000064;
	public static final int CFG_ITEM 									= 1000000065;
	public static final int CFG_COMPANYNAME 							= 1000000720;

	public static final int CTM_LOGINID 								= 4;
	public static final int CTM_CORPORATEID								= 1000000054;
	public static final int CTM_HRID									= 1000000069;
	public static final int CTM_INSTANCEID								= 179;
	public static final int CTM_FIRSTNAME								= 1000000019;
	public static final int CTM_LASTNAME								= 1000000018;
    public static final int CTM_FULLNAME								= 1000000017;
	public static final int CTM_COMPANYNAME 							= 1000000001;
	public static final int CTM_PPLORGANISATION							= 1000000010;
	public static final int CTM_SGRORGANISATION 						= 1000000014;
	public static final int CTM_DEPARTMENTNAME 							= 200000006;
	public static final int CTM_GROUPNAME								= 1000000015;
	public static final int CTM_REGIONNAME 								= 200000012;
	public static final int CTM_SITEID									= 1000000074;
	public static final int CTM_SITENAME								= 260000001;
	public static final int CTM_SITEGROUP								= 200000007;
	public static final int CTM_SGROUPROLE								= 1000000570;
	public static final int CTM_SGROUPID								= 1000000079;
	public static final int CTM_SGROUPDESCRIPTION						= 1000000000;
    public static final int CTM_SGROUPVENDOR							= 1000003745;
    public static final int CTM_SGROUPONCALL             				= 1000000902;
	public static final int CTM_PERSONID								= 1000000080;
	public static final int CTM_SGROUP_ASSOC_ROLE						= 1000000401;
	public static final int CTM_SGROUPALIAS								= 1000000293;
    public static final int CTM_Z1DACTION								= 1000000076;
	public static final int CTM_STATUS									= 7;
	public static final int CTM_COSTCENTER								= 300469300;
	public static final int CTM_ACCOUNTINGCODE							= 302006500;

	public static final int ASP_ACTION									= 1000000076;
	public static final int ASP_ENTITYNAME 								= 260100013;
	public static final int ASP_ROLENAME								= 260100005;
	public static final int ASP_PEOPLEID 								= 260100006;
	public static final int ASP_PEOPLE_INSTANCEID 						= 301104200;
	public static final int ASP_PEOPLE_FULLNAME 						= 260100003;
	public static final int ASP_ASSETID									= 260100008;
	public static final int ASP_AENTRYID								= 260100009;
	public static final int ASP_AINSTANCE								= 301104100;
	public static final int ASP_DATASET									= 301763400;
	public static final int ASP_CLASSID									= 400079600;
	public static final int ASP_STATUS									= 7;

	public static final int ASP_INVQ_INSTANCEID							= 301104100;
	public static final int ASP_INVQ_CLASSID							= 301168500;
	public static final int ASP_INVQ_TRACTION							= 250000021;
	public static final int ASP_INVQ_TRQUANTIY							= 250442101;

	public static final int COM_STATUS 									= 7;
	public static final int COM_COMPANYNAME								= 1000000001;
	public static final int COM_COMPANYTYPES 							= 1000000006;

	public static final int POR_STATUS									= 7;
	public static final int POR_COMPANYNAME								= 1000000001;
	public static final int POR_ORGANISATIONNAME						= 1000000010;
	public static final int POR_DEPARTMENTNAME							= 200000006;
	public static final int POR_DESCRIPTION								= 1000000000;

	public static final int SIT_STATUS									= 7;
	public static final int SIT_SITENAME								= 260000001;
	public static final int SIT_COUNTRY									= 1000000002;
	public static final int SIT_CITY									= 1000000004;

	public static final int BASE_SUMMARY								= 1000000000;
	public static final int BASE_NOTES									= 1000000151;
	public static final int BASE_STATUS									= 7;
	public static final int BASE_IMPACT									= 1000000163;
	public static final int BASE_URGENCY								= 1000000162;
	public static final int BASE_PRIORITY								= 1000000164;
	public static final int BASE_INSTANCEID								= 179;
	public static final int BASE_CUSTFIRSTNAME							= 1000000019;
	public static final int BASE_CUSTLASTNAME							= 1000000018;
	public static final int BASE_CUSTBUSSINESSPHONE						= 1000000056;
	public static final int BASE_CUSTCOMPANY							= 1000000082;
	public static final int BASE_CUSTORGANISATION						= 1000000010;
	public static final int BASE_CUSTDEPARTMENT							= 200000006;
	public static final int BASE_CUSTSITE								= 260000001;
	public static final int BASE_CUSTPERSONID 							= 1000000080;
	public static final int BASE_CATEGOP1								= 1000000063;
	public static final int BASE_CATEGOP2								= 1000000064;
	public static final int BASE_CATEGOP3								= 1000000065;
	public static final int BASE_CATEGPR1								= 200000003;
	public static final int BASE_CATEGPR2								= 200000004;
	public static final int BASE_CATEGPR3								= 200000005;
	public static final int BASE_PRODNAME								= 240001002;
	public static final int BASE_PRODVER								= 240001005;
	public static final int BASE_PRODMAN 								= 240001003;

	public static final int INC_INCIDENTNO								= 1000000161;
	public static final int INC_WEIGHT									= 1000000169;
	public static final int INC_CLASSCOMPANY							= 1000000001;
	public static final int INC_CLASSSERVICETYPE						= 1000000099;
	public static final int INC_ASSIGNCOMPANY							= 1000000251;
	public static final int INC_ASSIGNORGANISATION						= 1000000014;
	public static final int INC_ASSIGNGROUP								= 1000000217;
	public static final int INC_ASSIGNGROUPID							= 1000000079;	
	public static final int INC_ASSIGNPERSON							= 1000000218;
	public static final int INC_ASSIGNLOGINID							= 4;
	public static final int INC_OWNERCOMPANY							= 1000000426;
	public static final int INC_OWNERORGANISATION						= 1000000342;
	public static final int INC_OWNERGROUP								= 1000000422;
	public static final int INC_OWNERGROUPID							= 1000000427;
	public static final int INC_OWNERPERSON								= 1000000715;

	public static final int CHG_CHANGENO								= 1000000182;
	public static final int CHG_LOCATIONREGION 							= 200000012;
	public static final int CHG_LOCATIONSITE 							= 260000001;
	public static final int CHG_LOCATIONCOMPANY 						= 1000000001;
	public static final int CHG_CHANGERISKLEVEL							= 1000000180;
	public static final int CHG_CHANGECLASS 							= 1000000568;
	public static final int CHG_ASSIGNCMCOMPANY 						= 1000000251;
	public static final int CHG_ASSIGNCMORGANISATION 					= 1000000014;
	public static final int CHG_ASSIGNCMGROUP 							= 1000000015;
	public static final int CHG_ASSIGNCMGROUPID 						= 1000000079;
	public static final int CHG_ASSIGNCMPERSON 							= 1000000403;
	public static final int CHG_ASSIGNCMLOGINID 						= 1000000408;
	public static final int CHG_ASSIGNCCCOMPANY 						= 1000003228;
	public static final int CHG_ASSIGNCCORGANISATION 					= 1000003227;
	public static final int CHG_ASSIGNCCGROUP 							= 1000003229;
	public static final int CHG_ASSIGNCCGROUPID 						= 1000003234;
	public static final int CHG_ASSIGNCCPERSON 							= 1000003230;
	public static final int CHG_ASSIGNCCLOGINID 						= 1000003231;
	public static final int CHG_ASSIGNCOMPANY							= 1000003254;
	public static final int CHG_ASSIGNORGANISATION						= 1000003255;
	public static final int CHG_ASSIGNGROUP								= 1000003256;
	public static final int CHG_ASSIGNGROUPID							= 1000003259;
	public static final int CHG_ASSIGNPERSON							= 1000003257;
	public static final int CHG_ASSIGNLOGINID							= 1000003258;

	public static final int PBM_PROBLEMNO 								= 1000000232;
	public static final int PBM_INVESTIGATIONDRV 						= 1000000798;
	public static final int PBM_ASSIGNPCCOMPANY 						= 1000000834;
	public static final int PBM_ASSIGNPCORGANISATION 					= 1000000835;
	public static final int PBM_ASSIGNPCGROUP 							= 1000000837;
	public static final int PBM_ASSIGNPCGROUPID 						= 1000000427;
	public static final int PBM_ASSIGNPCPERSON 							= 1000000838;
	public static final int PBM_ASSIGNPCLOGINID 						= 1000000839;
	public static final int PBM_ASSIGNCOMPANY 							= 1000000251;
	public static final int PBM_ASSIGNORGANISATION 						= 1000000014;
	public static final int PBM_ASSIGNGROUP 							= 1000000217;
	public static final int PBM_ASSIGNGROUPID 							= 1000000079;
	public static final int PBM_ASSIGNPERSON 							= 1000000218;
	public static final int PBM_ASSIGNLOGINID 							= 303876100;
	public static final int PBM_WORKAROUND								= 1000000855;
	public static final int PBM_ROOTCAUSE								= 1000000744;
	public static final int PBM_KNECATEGORY								= 1000000984;
	public static final int PBM_KNOWNERRORID							= 1000000979;

	public String FORM_RUNTIME_ENTRY 									= "AREasy:Runtime Entry";
	public String FORM_RUNTIME_LOGGER 									= "AREasy:Status Logger";
	public String FORM_RUNTIME_OUTPUT 									= "AREasy:Status Output";
	public String FORM_DATA_SOURCE 										= "AREasy:Data Source";
	public String FORM_DATA_PARSER 										= "AREasy:Data Parser";
	public String FORM_DATA_MAPPING 									= "AREasy:Data Mapping";
	public String FORM_METADATA_ENTITIES 								= "AREasy:Metadata:Entities";
	public String FORM_METADATA_ATTRIBUTES 								= "AREasy:Metadata:Attributes";

	public int TARGET_ENTITY_FORM										= 0;
	public int TARGET_ENTITY_CLASS										= 1;
	public int TARGET_ENTITY_ACTION										= 2;
}
