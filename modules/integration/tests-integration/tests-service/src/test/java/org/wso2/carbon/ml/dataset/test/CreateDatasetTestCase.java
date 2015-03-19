/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.ml.dataset.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.ml.integration.common.utils.MLIntegrationBaseTest;
import org.wso2.carbon.ml.integration.common.utils.MLIntegrationTestConstants;

public class CreateDatasetTestCase extends MLIntegrationBaseTest {

    @BeforeClass(alwaysRun = true, groups = "wso2.ml.integration")
    public void initTest() throws Exception {
        super.init();
    }

    /**
     * Test creating a dataset from a valid csv file.
     * 
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test(groups = "wso2.ml.integration", description = "Create a dataset from a CSV file")
    public void testCreateDatasetFromFile() throws ClientProtocolException, IOException, URISyntaxException {
        CloseableHttpResponse response = uploadDatasetFromCSV("ForestCoverDataset1", "1.0", "data/fcSample.csv");
        Assert.assertEquals(MLIntegrationTestConstants.HTTP_OK, response.getStatusLine().getStatusCode());
        response.close();
    }
    
    /**
     * Test Creating a new version of an existing dataset
     * 
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test(groups = "wso2.ml.integration", description = "Create a new version of an existing dataset",
            dependsOnMethods="testCreateDatasetFromFile")
    public void testCreateNewDatasetVersion() throws ClientProtocolException, IOException, URISyntaxException {
        CloseableHttpResponse response = uploadDatasetFromCSV("ForestCoverDataset1", "2.0", "data/fcSample.csv");
        Assert.assertEquals(MLIntegrationTestConstants.HTTP_OK, response.getStatusLine().getStatusCode());
        response.close();
    }
    
    /**
     * Test creating a dataset from a non-existing csv file.
     * 
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test(groups = "wso2.ml.integration", description = "Create a dataset from a non-existing CSV file")
    public void testCreateDatasetFromNonExistingFile() throws ClientProtocolException, IOException, URISyntaxException {
        CloseableHttpResponse response = uploadDatasetFromCSV("ForestCoverDataset2", "1.0", "data/xxx.csv");
        Assert.assertEquals(MLIntegrationTestConstants.HTTP_INTERNAL_SERVER_ERROR, response.getStatusLine()
                .getStatusCode());
        response.close();
    }
    
    /**
     * Test creating a dataset from a WSO2 BAM table.
     * 
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test(groups = "wso2.ml.integration", description = "Create a dataset from a WSO2 BAM Table",
            dependsOnMethods="testCreateDatasetFromNonExistingFile")
    public void testCreateDatasetFromBam() throws ClientProtocolException, IOException, URISyntaxException {
        String bamTableUrl = System.getProperty("bam.table.url");
        if (bamTableUrl == null || bamTableUrl.isEmpty()) {
            throw new SkipException("Skipping tests because WSO2 BAM table is not available.");
        }
        String payload = "{\"name\" : \"ForestCoverDataset3\",\"dataSourceType\" : \"bam\",\"dataTargetType\" : "
                        + "\"file\"," + "\"sourcePath\" : \""+ bamTableUrl + "\",\"dataType\""
                        + " : \"csv\"," + "\"comments\" : \"fcSample\",\"version\" : \"1.0\"}";
        CloseableHttpResponse response = doHttpPost(new URI("https://localhost:9443/api/datasets"), payload);
        Assert.assertEquals(MLIntegrationTestConstants.HTTP_OK, response.getStatusLine().getStatusCode());
        response.close();
    }
    
}