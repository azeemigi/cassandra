/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.cassandra.db.migration;

import java.io.IOException;
import java.util.Collection;

import org.apache.cassandra.config.CFMetaData;
import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.config.KSMetaData;
import org.apache.cassandra.config.Schema;
import org.apache.cassandra.db.RowMutation;

public class  AddColumnFamily extends Migration
{
    private final CFMetaData cfm;

    public AddColumnFamily(CFMetaData cfm) throws ConfigurationException
    {
        super(System.nanoTime());

        KSMetaData ksm = Schema.instance.getTableDefinition(cfm.ksName);

        if (ksm == null)
            throw new ConfigurationException(String.format("Can't add ColumnFamily '%s' to Keyspace '%s': Keyspace does not exist.", cfm.cfName, cfm.ksName));
        else if (ksm.cfMetaData().containsKey(cfm.cfName))
            throw new ConfigurationException(String.format("Can't add ColumnFamily '%s' to Keyspace '%s': Already exists.", cfm.cfName, cfm.ksName));
        else if (!Migration.isLegalName(cfm.cfName))
            throw new ConfigurationException("Can't add ColumnFamily '%s' to Keyspace '%s': Invalid ColumnFamily name.");

        this.cfm = cfm;
    }

    protected Collection<RowMutation> applyImpl() throws ConfigurationException, IOException
    {
        return MigrationHelper.addColumnFamily(cfm, timestamp);
    }

    @Override
    public String toString()
    {
        return "Add column family: " + cfm.toString();
    }
}
