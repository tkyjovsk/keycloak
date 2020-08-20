<!--
~ Copyright 2016 Red Hat, Inc. and/or its affiliates
~ and other contributors as indicated by the @author tags.
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                version="2.0"
                exclude-result-prefixes="xalan #all">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" xalan:indent-amount="4" standalone="no"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="//*[local-name()='infinispan']/*[local-name()='cache-container' and @name='default']">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()" />

        </xsl:copy>
    </xsl:template>

    <!--    <xsl:template match="//*[local-name()='infinispan']/*[local-name()='cache-container' and @name='default']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />

            <security>
                <authorization>
                    <identity-role-mapper/>
                    <role name="___script_manager" permissions="ALL"/>
                </authorization>
            </security>

            <xsl:apply-templates select="node()" />

        </xsl:copy>
    </xsl:template>-->
    
    <xsl:template match="//*[local-name()='infinispan']/*[local-name()='cache-container' and @name='default']">
        <xsl:copy>
            <xsl:apply-templates select="@*" />

            <security>
                <authorization>
                    <identity-role-mapper/>
                    <role name="keycloak" permissions="ALL"/>
                </authorization>
            </security>

            <xsl:apply-templates select="node()" />

        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="//*[local-name()='hotrod-connector' and @name='hotrod']">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()" />
            <!--     Add "authentication" into HotRod connector configuration -->
            <authentication security-realm="default">
                <sasl mechanisms="DIGEST-MD5" qop="auth" server-name="keycloak-jdg-server">
                    <policy>
                        <no-anonymous value="false" />
                    </policy>
                </sasl>
            </authentication>
        </xsl:copy>
    </xsl:template>
    

    <!-- Add "AllowScriptManager" security-realm -->
    <!--    <xsl:template match="//*[local-name()='infinispan']/*[local-name()='server']/*[local-name()='security']/*[local-name()='security-realms']">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()" />

            <xsl:element name="security-realm" namespace="{namespace-uri()}">
                <xsl:attribute name="name">AllowScriptManager</xsl:attribute>
                <xsl:element name="authentication" namespace="{namespace-uri()}">
                    <xsl:element name="users" namespace="{namespace-uri()}">
                        <xsl:element name="user" namespace="{namespace-uri()}">
                            <xsl:attribute name="username">___script_manager</xsl:attribute>
                            <xsl:element name="password" namespace="{namespace-uri()}">not-so-secret-password</xsl:element>
                        </xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:copy>
    </xsl:template>-->

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>