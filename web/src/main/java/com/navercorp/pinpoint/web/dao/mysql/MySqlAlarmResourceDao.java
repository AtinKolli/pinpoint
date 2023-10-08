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

package com.navercorp.pinpoint.web.dao.mysql;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.navercorp.pinpoint.web.alarm.vo.AlarmEmp;
import com.navercorp.pinpoint.web.alarm.vo.Rule;
import com.navercorp.pinpoint.web.dao.AlarmResourceDao;

@Repository
public class MySqlAlarmResourceDao implements AlarmResourceDao {

    private static final String NAMESPACE = AlarmResourceDao.class.getPackage().getName() + "." + AlarmResourceDao.class.getSimpleName() + ".";

    @Autowired
    @Qualifier("sqlSessionTemplate")
    private SqlSessionTemplate sqlSessionTemplate;

    public SqlSessionTemplate getSqlSessionTemplate() {
        return sqlSessionTemplate;
    }

    public List<Rule> selectAppRule(String applicationName) {
        return getSqlSessionTemplate().selectList(NAMESPACE + "selectRules", applicationName);
    }
    
    public void insertAppRule(List<Rule> rules) {
        getSqlSessionTemplate().selectList(NAMESPACE + "insertAppRule", rules);
    }

    public void deleteAppRule(String applicationName) {
        getSqlSessionTemplate().selectList(NAMESPACE + "deleteAppRule", applicationName);
    }
    
    public List<String> selectEmpGroupPhoneNumber(String empGroup) {
        return getSqlSessionTemplate().selectList(NAMESPACE + "selectEmpGroupPhoneNumber", empGroup);
    }
    
    public List<String> selectEmpGroupEmail(String empGroup) {
        return getSqlSessionTemplate().selectList(NAMESPACE + "selectEmpGroupEmail", empGroup);
    }
    
    public List<String> selectEmpGroupName() {
        return getSqlSessionTemplate().selectList(NAMESPACE + "selectAlarmGroupList");
    }

    public List<AlarmEmp> selectEmpGroupMember(String alarmGroup) {
        return getSqlSessionTemplate().selectList(NAMESPACE + "selectAlarmGroupMember", alarmGroup);
    }

    public void insertEmpGroupMember(List<AlarmEmp> emps) {
        getSqlSessionTemplate().insert(NAMESPACE + "insertAlarmGroupMember", emps);
    }

    public void deleteEmpGroupMember(String groupName) {
        getSqlSessionTemplate().insert(NAMESPACE + "deleteAlarmGroupMember", groupName);
    }
}
