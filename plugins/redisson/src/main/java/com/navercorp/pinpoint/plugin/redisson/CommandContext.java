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
package com.navercorp.pinpoint.plugin.redisson;

/**
 * @author jaehong.kim
 */
public class CommandContext {
    private long beginTime;
    private long endTime;
    private boolean fail;

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isFail() {
        return fail;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    public long getElapsedTime() {
        long result = endTime - beginTime;
        return result > 0 ? result : 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{beginTime=");
        builder.append(beginTime);
        builder.append(", endTime=");
        builder.append(endTime);
        builder.append(", fail=");
        builder.append(fail);
        builder.append(", elapsed=");
        builder.append(getElapsedTime());
        builder.append("}");
        return builder.toString();
    }
}