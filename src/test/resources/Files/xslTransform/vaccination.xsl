<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:strip-space elements="*"/>
	<xsl:template match="/">
		<Bundle xmlns="http://hl7.org/fhir">
		   <entry>
		      <resource>
		         <Composition>
		            <language value="{ClinicalDocument/languageCode/@code}"/>
		            <type>
		               <coding>
		                  <system value="http://loinc.org"/>
		                  <code value="{ClinicalDocument/code/@code}"/>
		                  <display value="{ClinicalDocument/code/@displayName}"/>
		               </coding>
		            </type>
		            <date value="{concat(substring(ClinicalDocument/effectiveTime/@value, 1, 4), '-', substring(ClinicalDocument/effectiveTime/@value, 5, 2), '-', substring(ClinicalDocument/effectiveTime/@value, 7, 2), 'T', substring(ClinicalDocument/effectiveTime/@value, 9, 2), ':', substring(ClinicalDocument/effectiveTime/@value, 11, 2), ':', substring(ClinicalDocument/effectiveTime/@value, 13, 2), substring(ClinicalDocument/effectiveTime/@value, 15, 3), ':', substring(ClinicalDocument/effectiveTime/@value, 18, 2))}"/>
		            <title value="{ClinicalDocument/title}"/>
		            <confidentiality value="{ClinicalDocument/confidentialityCode/@code}"/>
		            <relatesTo>
		               <code value="transforms"/>
		               <targetIdentifier>
		                  <system value="{ClinicalDocument/id/@root}"/>
		                  <value value="{ClinicalDocument/id/@extension}"/>
		               </targetIdentifier>
		            </relatesTo>
		            <section>
		               <title value="{ClinicalDocument/component/structuredBody/component/section/title}"/>
		               <code>
		                  <coding>
		                     <system value="http://loinc.org"/>
		                     <code value="{ClinicalDocument/component/structuredBody/component/section/code/@code}"/>
		                     <display value="{ClinicalDocument/component/structuredBody/component/section/code/@displayName}"/>
		                  </coding>
		               </code>
		            </section>
		            <section>
		               <code>
		               <coding>
		                  <system value="http://loinc.org"/>
		                  <code value="{ClinicalDocument/code/@code}"/>
		                  <display value="{ClinicalDocument/code/@displayName}"/>
		               </coding>
		               </code>
		            </section>
		         </Composition>
		      </resource>
		   </entry>		
			<xsl:apply-templates/>
		</Bundle>
	</xsl:template>

	<xsl:template match="title">
	</xsl:template>
	
	<xsl:template match="recordTarget">
		<entry>
		   <resource>
		      <Patient>
		         <identifier>
		            <system value="{patientRole/id/@root}"/>
		            <value value="{patientRole/id/@extension}"/>
		         </identifier>
		         <name>
			         <family value="{patientRole/patient/name/family}"/>
		            <given value="{patientRole/patient/name/given}"/>
		         </name>
				<xsl:choose>
					<xsl:when test="patientRole/patient/administrativeGenderCode/@code='M'">
						<gender value="male"/>
					</xsl:when>
					<xsl:when test="patientRole/patient/administrativeGenderCode/@code='F'">
						<gender value="female"/>
					</xsl:when>
					<xsl:otherwise>
						<gender value="unknown"/>
					</xsl:otherwise>
				</xsl:choose>
		         
		         <birthDate value="{concat(substring(patientRole/patient/birthTime/@value, 1,4), '-', substring(patientRole/patient/birthTime/@value, 5,2), '-', substring(patientRole/patient/birthTime/@value, 7,2))}"/>
		      </Patient>
		   </resource>
		</entry>
	</xsl:template>
	
	<xsl:template match="author">
	   <entry>
	      <resource>
	         <Practitioner>
	            <identifier>
	               <system value="{assignedAuthor/id/@root}"/>
	               <value value="{assignedAuthor/id/@extension}"/>
	            </identifier>
	            <name>
	               <family value="{assignedAuthor/assignedPerson/name/family}"/>
	               <given value="{assignedAuthor/assignedPerson/name/given}"/>
	               <prefix value="{assignedAuthor/assignedPerson/name/prefix}"/>
	            </name>
	         </Practitioner>
	      </resource>
	   </entry>
	</xsl:template>
	
	<xsl:template match="custodian">
	   <entry>
	      <resource>
	         <Organization>
	            <identifier>
	               <system value="{assignedCustodian/representedCustodianOrganization/id/@root}"/>
	               <value value="{assignedCustodian/representedCustodianOrganization/id/@extension}"/>
	            </identifier>
	            <name value="{assignedCustodian/representedCustodianOrganization/name}"/>
	         </Organization>
	      </resource>
	   </entry>
	</xsl:template>
	
	<xsl:template match="component">
		component not handled
	</xsl:template>
	
	
</xsl:stylesheet>