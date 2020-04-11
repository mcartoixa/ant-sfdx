/*
 * Copyright 2020 Mathieu Cartoixa.
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
package com.mcartoixa.ant.sfdx.force.alias;

/**
 *
 * @author Mathieu Cartoixa
 */
public class Alias {

    public Alias() {
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param alias the value to set
     */
    public void setValue(final String alias) {
        this.value = alias;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", alias, value);
    }

    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    private String alias;
    private String value;
}
