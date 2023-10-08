/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.mapper;

import static com.navercorp.pinpoint.common.hbase.HBaseTables.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.navercorp.pinpoint.common.bo.AgentStatCpuLoadBo;
import com.navercorp.pinpoint.common.bo.AgentStatMemoryGcBo;
import com.navercorp.pinpoint.thrift.dto.TAgentStat;
import com.navercorp.pinpoint.thrift.dto.TJvmGc;
import com.navercorp.pinpoint.web.vo.AgentStat;

import org.apache.hadoop.hbase.client.Result;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Component;

/**
 * @author harebox
 * @author hyungil.jeong
 */
@Component
public class AgentStatMapper implements RowMapper<List<AgentStat>> {

    private TProtocolFactory factory = new TCompactProtocol.Factory();

    public List<AgentStat> mapRow(Result result, int rowNum) throws Exception {
        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        Map<byte[], byte[]> qualifierMap = result.getFamilyMap(AGENT_STAT_CF_STATISTICS);
        // FIXME (2014.08) Legacy support for TAgentStat Thrift DTO stored directly into hbase.
        if (qualifierMap.containsKey(AGENT_STAT_CF_STATISTICS_V1)) {
            return readAgentStatThriftDto(qualifierMap.get(AGENT_STAT_CF_STATISTICS_V1));
        }

        AgentStat agentStat = new AgentStat();
        if (qualifierMap.containsKey(AGENT_STAT_CF_STATISTICS_MEMORY_GC)) {
            AgentStatMemoryGcBo.Builder builder = new AgentStatMemoryGcBo.Builder(qualifierMap.get(AGENT_STAT_CF_STATISTICS_MEMORY_GC));
            agentStat.setMemoryGc(builder.build());
        }
        if (qualifierMap.containsKey(AGENT_STAT_CF_STATISTICS_CPU_LOAD)) {
            AgentStatCpuLoadBo.Builder builder = new AgentStatCpuLoadBo.Builder(qualifierMap.get(AGENT_STAT_CF_STATISTICS_CPU_LOAD));
            agentStat.setCpuLoad(builder.build());
        }
        List<AgentStat> agentStats = new ArrayList<AgentStat>();
        agentStats.add(agentStat);
        return agentStats;
    }

    // FIXME (2014.08) Legacy support for TAgentStat Thrift DTO stored directly into hbase.
    private List<AgentStat> readAgentStatThriftDto(byte[] tAgentStatByteArray) throws TException {
        // CompactProtocol used
        TDeserializer deserializer = new TDeserializer(factory);
        TAgentStat tAgentStat = new TAgentStat();
        deserializer.deserialize(tAgentStat, tAgentStatByteArray);
        TJvmGc gc = tAgentStat.getGc();
        if (gc == null) {
            return Collections.emptyList();
        }
        AgentStatMemoryGcBo.Builder memoryGcBoBuilder = new AgentStatMemoryGcBo.Builder(tAgentStat.getAgentId(), tAgentStat.getStartTimestamp(), tAgentStat.getTimestamp());
        memoryGcBoBuilder.gcType(gc.getType().name());
        memoryGcBoBuilder.jvmMemoryHeapUsed(gc.getJvmMemoryHeapUsed());
        memoryGcBoBuilder.jvmMemoryHeapMax(gc.getJvmMemoryHeapMax());
        memoryGcBoBuilder.jvmMemoryNonHeapUsed(gc.getJvmMemoryNonHeapUsed());
        memoryGcBoBuilder.jvmMemoryNonHeapMax(gc.getJvmMemoryNonHeapMax());
        memoryGcBoBuilder.jvmGcOldCount(gc.getJvmGcOldCount());
        memoryGcBoBuilder.jvmGcOldTime(gc.getJvmGcOldTime());

        AgentStat agentStat = new AgentStat();
        agentStat.setMemoryGc(memoryGcBoBuilder.build());

        List<AgentStat> result = new ArrayList<AgentStat>(1);
        result.add(agentStat);
        return result;
    }

}
