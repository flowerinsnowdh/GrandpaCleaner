/* Copyright (C) 2025 flowerinsnow
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.flowerinsnow.grandpacleaner.util;

import java.util.logging.Logger;

public final class LogUtil {
    private LogUtil() {
    }

    public static void throwing(Logger logger, Throwable throwable) {
        LogUtil.throwing(logger, throwable, false);
    }

    private static void throwing(Logger logger, Throwable throwable, boolean causedBy) {
        logger.severe((causedBy ? "Caused by: " : "") + throwable);
        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            logger.severe("\tat " + stackTraceElement.toString());
        }
        if (throwable.getCause() != null) {
            LogUtil.throwing(logger, throwable.getCause(), true);
        }
    }
}
