/*
 * Copyright 2018 Mathieu Cartoixa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcartoixa.ant.sfdx.force.org;

/**
 *
 * @author Mathieu Cartoixa
 */
public class Organization {

    public Organization() {
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias the alias to set
     */
    public void setAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * @return the devHubOrgId
     */
    public String getDevHubOrgId() {
        return devHubOrgId;
    }

    /**
     * @param devHubOrgId the devHubOrgId to set
     */
    public void setDevHubOrgId(final String devHubOrgId) {
        this.devHubOrgId = devHubOrgId;
    }

    /**
     * @return the expirationDate
     */
    public String getExpirationDate() {
        return expirationDate;
    }

    /**
     * @param expirationDate the expirationDate to set
     */
    public void setExpirationDate(final String expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * @param orgId the orgId to set
     */
    public void setOrgId(final String orgId) {
        this.orgId = orgId;
    }

    /**
     * @return the orgName
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * @param orgName the orgName to set
     */
    public void setOrgName(final String orgName) {
        this.orgName = orgName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(final String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (%s) %s [%s]",
                alias == null ? "" : alias,
                orgId,
                orgName == null ? "" : orgName,
                userName
        );
    }

    private String alias;
    private String devHubOrgId;
    private String expirationDate;
    private String orgId;
    private String orgName;
    private String userName;
}
