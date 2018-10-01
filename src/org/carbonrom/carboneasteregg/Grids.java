/*
 * Copyright (C) 2018 CarbonROM
 * Copyright (C) 2018 Adin Kwok (adinkwok)
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

package org.carbonrom.carboneasteregg;

import java.util.Random;

public class Grids {
    private static final byte[] singleGrid = new byte[]{
            1, 2, 3, 4, 5,
            6, 7, 8, 9, 10,
            11, 12, 13, 14, 15,
            16, 17, 18, 19, 20,
            21, 22, 23, 24, 25
    };
    public static byte[] generatedPattern;

    public static void createPattern() {
        Random rnd = new Random();
        generatedPattern = singleGrid;
        for (int i = generatedPattern.length - 1; i > 0; i--) {
            byte index = (byte) rnd.nextInt(i + 1);
            byte a = generatedPattern[index];
            generatedPattern[index] = generatedPattern[i];
            generatedPattern[i] = a;
        }
    }
}
