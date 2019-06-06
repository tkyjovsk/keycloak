/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.keycloak.services.util;

import java.util.regex.Pattern;

import org.keycloak.sessions.AuthenticationSessionModel;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AuthenticationFlowHistoryHelper {

    /**
     * Authentication session note with the list of IDs of successful authentication action executions.
     * Those corresponds with the authenticator executions, which were shown to the user.
     *
     * IDs are divided by {@link #DELIMITER}
     */
    private static final String SUCCESSFUL_ACTION_EXECUTIONS = "successful.action.executions";


    private static final String DELIMITER = "::";

    // Just perf optimization
    private static final Pattern PATTERN = Pattern.compile(DELIMITER);


    public static void pushLastSuccessfulExecution(AuthenticationSessionModel authenticationSession, String executionId) {
        String history = authenticationSession.getAuthNote(SUCCESSFUL_ACTION_EXECUTIONS);

        history = (history == null) ? executionId : history + DELIMITER + executionId;
        authenticationSession.setAuthNote(SUCCESSFUL_ACTION_EXECUTIONS, history);
    }


    public static boolean hasLastSuccessfulExecution(AuthenticationSessionModel authenticationSession) {
        return authenticationSession.getAuthNote(SUCCESSFUL_ACTION_EXECUTIONS) != null;
    }


    public static String pullLastSuccessfulExecution(AuthenticationSessionModel authenticationSession) {
        String history = authenticationSession.getAuthNote(SUCCESSFUL_ACTION_EXECUTIONS);

        if (history == null) {
            return null;
        }

        String[] splits = PATTERN.split(history);

        String lastActionExecutionId = splits[splits.length - 1];

        if (splits.length == 1) {
            authenticationSession.removeAuthNote(SUCCESSFUL_ACTION_EXECUTIONS);
        } else {
            String newHistory = history.substring(0, history.length() - DELIMITER.length() - lastActionExecutionId.length());
            authenticationSession.setAuthNote(SUCCESSFUL_ACTION_EXECUTIONS, newHistory);
        }

        return lastActionExecutionId;
    }

}
