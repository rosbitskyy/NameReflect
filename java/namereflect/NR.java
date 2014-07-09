/* Copyright 2012 Rosbitskyy Ruslan (r@rrs.pp.ua).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package namereflect.trunk.java.namereflect;

import java.util.List;

/**
 * @User: Ruslan Rosbitskyy
 * @Date: 09.07.14 18:55
 * Project: test-case
 * Developed by R.Rosbitskyy
 *
 * @usage:
 * NameReflect n = new NameReflect(null)
 * render n.getCaseList();
 *
 * NameReflect n = new NameReflect("Розбицький Руслан Станіславович")
 * render n.getCaseList();
 * @return ['Розбицький Руслан Станіславович', 'Розбицького Руслана Станіславовича',
 *          'Розбицькому Русланові Станіславовичу', 'Розбицького Руслана Станіславовича',
 *          'Розбицьким Русланом Станіславовичем', 'Розбицькому Русланові Станіславовичу',
 *          'Розбицький Руслане Станіславовичу']
 *
 * render NR.list("Розбицький Руслан Станіславович");
 * @return ['Розбицький Руслан Станіславович', 'Розбицького Руслана Станіславовича',
 *          'Розбицькому Русланові Станіславовичу', 'Розбицького Руслана Станіславовича',
 *          'Розбицьким Русланом Станіславовичем', 'Розбицькому Русланові Станіславовичу',
 *          'Розбицький Руслане Станіславовичу']
 *
 * render NR.kimChim("Розбицький Руслан Станіславович");
 * @return 'Розбицьким Русланом Станіславовичем'
 */
public class NR {

    /**
     * Вертає масив відмінків ПІБ
     *
     * @param name - ПІБ, чи будьяка комбінація частин призвища чі то ім'я чи побатькові
     *             (наприклад Розбицький Руслан)
     * @return ['Розбицький Руслан Станіславович', 'Розбицького Руслана Станіславовича',
     * 'Розбицькому Русланові Станіславовичу', 'Розбицького Руслана Станіславовича',
     * 'Розбицьким Русланом Станіславовичем', 'Розбицькому Русланові Станіславовичу',
     * 'Розбицький Руслане Станіславовичу']
     * @usage NR.list("Розбицький Руслан Станіславович");
     */
    public static List<String> list(String name) {
        NameReflect nameReflect = new NameReflect(name);
        return nameReflect.getCaseList();
    }

    /**
     * Вертає вираз у називному відмінку
     * @param name
     * @usage NR.kto("Розбицький Руслан Станіславович");
     * @return 'Розбицький Руслан Станіславович'
     */
    public static String kto(String name) {
        NameReflect n = new NameReflect(name);
        return n.getCaseList(NameReflect.UANAZYVNYI);
    }

    /**
     * Вертає вираз у родовому відмінку
     * @param name
     * @usage NR.kto("Розбицький Руслан Станіславович");
     * @return 'Розбицького Руслана Станіславовича'
     */
    public static String kogo(String name) {
        NameReflect n = new NameReflect(name);
        return n.getCaseList(NameReflect.UARODOVYI);
    }

    /**
     * Вертає вираз у родовому відмінку
     * @param name
     * @usage NR.komy("Розбицький Руслан Станіславович");
     * @return 'Розбицькому Русланові Станіславовичу'
     */
    // давадьный
    public static String komy(String name) {
        NameReflect n = new NameReflect(name);
        return n.getCaseList(NameReflect.UADAVALNYI);
    }

    /**
     * Вертає вираз у знахідному відмінку
     * @param name
     * @usage NR.kogoScho("Розбицький Руслан Станіславович");
     * @return 'Розбицького Руслана Станіславовича'
     */
    public static String kogoScho(String name) {
        NameReflect n = new NameReflect(name);
        return n.getCaseList(NameReflect.UAZNAHIDNYI);
    }

    /**
     * Вертає вираз у орудному відмінку
     * @param name
     * @usage NR.kem("Розбицький Руслан Станіславович");
     * @usage NR.kim("Розбицький Руслан Станіславович");
     * @usage NR.kum("Розбицький Руслан Станіславович");
     * @return 'Розбицьким Русланом Станіславовичем'
     */
    public static String kem(String name) {
        return kim(name);
    }
    public static String kum(String name) {
        return kim(name);
    }

    public static String kim(String name) {
        NameReflect n = new NameReflect(name);
        return n.getCaseList(NameReflect.UAORUDNYI);
    }

    /**
     * Вертає вираз у місцевому відмінку
     * @param name
     * @usage NR.naKomy("Розбицький Руслан Станіславович");
     * @return 'Розбицькому Русланові Станіславовичу'
     */
    public static String naKomy(String name) {
        NameReflect n = new NameReflect(name);
        return n.getCaseList(NameReflect.UAMISZEVYI);
    }

    // кличний
    public static String kogoK(String name) {
        NameReflect n = new NameReflect(name);
        return n.getCaseList(NameReflect.UAKLYCHNYI);
    }
}
