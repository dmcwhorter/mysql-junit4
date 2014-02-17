package com.groupon.mysql.testing;

/*
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.util.IntegerMapper;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

public class MySQLRuleTest
{
    @Test
    public void testOneOffDatabaseAccessibleAfterBefore() throws Exception
    {
        MySQLRule rule = MySQLRule.oneOff();
        rule.before();

        DataSource ds = rule.getDataSource();
        try (Handle h = DBI.open(ds)) {
            int two = h.createQuery("select 2").map(IntegerMapper.FIRST).first();
            assertThat(two).isEqualTo(2);
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testAfterShutsDownOneOffDatabase() throws Exception
    {
        MySQLRule rule = MySQLRule.oneOff();
        rule.before();
        rule.after();

        DataSource ds = rule.getDataSource();
        try (Handle h = DBI.open(ds)) {
            h.createQuery("select 2").map(IntegerMapper.FIRST).first();
        }
    }

    @Test
    public void testAfterHasNoEffectOnGlobal() throws Exception
    {
        MySQLRule mysql = MySQLRule.global();
        mysql.before();
        mysql.after();
        DataSource ds = mysql.getDataSource();
        try (Handle h = DBI.open(ds)) {
            int two = h.createQuery("select 2").map(IntegerMapper.FIRST).first();
            assertThat(two).isEqualTo(2);
        }
    }

    @Test
    public void testGlobalIsGlobal() throws Exception
    {
        MySQLRule one = MySQLRule.global();
        MySQLRule two = MySQLRule.global();

        assertThat(one).isSameAs(two);
    }
}