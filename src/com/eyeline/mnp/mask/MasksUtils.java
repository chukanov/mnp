package com.eyeline.mnp.mask;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/** Utilities to create masks from digits range
 * @author Chukanov
 */
public class MasksUtils {

    private MasksUtils(){}

    /** Build HashSet of address masks
     * min and max values are included in the range
     * Example:
     * min = 8000000, max = 8099999, capability = 100000 returns { 80????? }
     *
     * @param min start of the range
     * @param max end of the range
     * @param capability number capability between min and max
     * @return LinkedHashSet address masks
     * @throws IllegalArgumentException if invalid params or capability doesn't match min and max range values
     */
    public static Set<Mask> buildMasksByRange(String min, String max, int capability) {
        if (min == null || min.length()==0) throw new IllegalArgumentException("min == null");
        if (capability <=0 ) throw new IllegalArgumentException("invalid capability "+capability);
        if (min.length()!=max.length() ) throw new IllegalArgumentException("min len!= max.len ("+min+","+max+")");

        Set<Mask> result = new LinkedHashSet<>();
        if (capability == 1) {
            result.add(new Mask(min));
            return result;
        }
        int differenceIndex = findDifferenceIndex(min, max);
        String changeableString = min.substring(differenceIndex);
        String prefix = min.substring(0, differenceIndex);

        int changeable = Integer.valueOf(changeableString);
        int maxChangeableValue = Integer.valueOf(max.substring(differenceIndex));
        if (maxChangeableValue - changeable - capability + 1 !=0) throw new IllegalArgumentException("invalid capability "+capability+" min="+min+", max="+max);

        LinkedList<Integer> changeableStack = new LinkedList<Integer>();

         //convert "changeable" to stack by powers of 10
        buildPower10Stack(changeable, changeableStack);
        LinkedList<Integer> capabilityStack = spreadCapabilityByPower10(changeableStack, capability);

        while (capabilityStack.size()>0) {
            int nextCapability = capabilityStack.removeLast();
            int zeroCount = (int) Math.log10(nextCapability);
            int capabilityBase = (int)Math.pow(10, zeroCount);
            if (zeroCount > 0) {
                nextCapability = nextCapability/capabilityBase;
            }
            String questionString = buildRepeatedString(zeroCount, Character.toString(Mask.WILDCARD));
            for (int i=0; i<nextCapability; i++){
                int chMultiplier = changeable/capabilityBase;
                String s;
                String chMultiplierStr = Integer.toString(chMultiplier);
                int resultStringLen = prefix.length() + chMultiplierStr.length() + zeroCount;
                if (resultStringLen == min.length()) {          //ordinary case
                    s = prefix + chMultiplierStr + questionString;
                } else if (resultStringLen > min.length()) {    //case of 1 mask for range
                    s = prefix + questionString;
                } else {                                        //case of 0000001 in changeable
                    int difference = min.length() - resultStringLen;
                    s = prefix + buildRepeatedString(difference, "0") + chMultiplierStr + questionString;
                }
                changeable += capabilityBase;
                result.add(new Mask(s));
            }
        }
        return result;
    }

    /** calculates masks by min value and capability
     * @see @method buildMasksByRange(String min, String max, int capability)
     * @param min start of the range
     * @param capability number capability between min and max
     * @return LinkedHashSet address masks
     * @throws IllegalArgumentException if invalid params
     */
    public static Set<Mask> buildMasksByRange(String min, int capability) {
        long minL = Long.valueOf(min);
        long maxL = minL+capability-1;
        return buildMasksByRange(min, Long.toString(maxL), capability);
    }

    /** calculates masks by min value and capability
     * @see @method buildMasksByRange(String min, String max, int capability)
     * @param min start of the range
     * @param max end of the range
     * @return LinkedList address masks
     * @throws IllegalArgumentException if invalid params
     */
    public static Set<Mask> buildMasksByRange(String min, String max) {
        long minL = Long.valueOf(min);
        long maxL = Long.valueOf(max);
        if (minL > maxL) throw new IllegalArgumentException("min > max ("+min+">"+max+")");
        return buildMasksByRange(min, max, (int) (maxL - minL)+1);
    }


    private static String buildRepeatedString(int count, String str) {
        StringBuilder questionStringBuilder = new StringBuilder();
        for (int i=0; i<count; i++) {
            questionStringBuilder.append(str);
        }
        return questionStringBuilder.toString();
    }

    private static int findDifferenceIndex(String min, String max){
        char[] minChars = min.toCharArray();
        char[] maxChars = max.toCharArray();
        for (int i=0; i<minChars.length; i++) {
            if (minChars[i]!=maxChars[i]){
                return i;
            }
        }
        return min.length()-1;
    }

    private static void buildPower10Stack(int number, LinkedList<Integer> stack) {
        int countZero = (int)Math.log10(number);
        if (countZero<0) {
            stack.add(number);
            return;
        }
        int powBase = (int)Math.pow(10, countZero);
        int powMultiplier = number/powBase;
        int powMultiBase = powMultiplier * powBase;
        int rest = number - powMultiBase;
        stack.add(powMultiBase);
        if (rest != 0) {
            buildPower10Stack(rest, stack);
        }
    }
  /*
  Это центральный метод в котором собственно происходит вся магия.
  Этот метод распределяет емкость номеров последовательно на слагаемые, которыми мы будем добивать минимальное число.
  По этим слагаемым в последствии будем ориентирвоаться сколько знаков вопроса нужно написать.
  Как это работает:
  Допустим у нас минимальное значение 8809
  Тогда 10-ный стек этого числа будет: {8000, 800, 9}
  Пускай нам надо распределить емкость 1191 (т.е. от 8809 до 9999)
  Мы берем 9 - последний элемент стека
  Нам эту 9 надо добить вплоть до минимальной степени 10 следующего элемента, тоесть до 100 (следующий элемент - 800)
  Считаем сколько надо до нее добивать 100 - 9 = 91
  91 разлагаем также по степеням 10, выходит, что нам надо последовательно добавить к минимальному значению
  сначала 1, потом 90.
  На этом 9 отработана.
  В конце ее отработки, раз уж мы добили 9 до 100, то увеличиваем следующий элемент + 1*(10^(степень этого элемента)
  В итоге у нас следующее слагаемое 800 превращается в 900

  Далее новая итерация - надо 900 добить до 1000
  Тут мы в результат запишем 100
  и 8000 поменяем на 9000

  и далее 9000 до 10000
  в результат запишем 1000
  Но это все почти идеальный случай, когда степень минимума = степени максимума, этот алгоритм без проблем все как надо разложит

  Случай когда минимум = 1, а максимум 9999999. Тут все ломается
  мы от 1 добьем до 10
  а потом надо будет добивать сразу до 9999990
  для этого надо будет распилить в цикле по 9*10(i+1)
  и потом остаток снова распилить

   P.S.: Наверно это будет понятней, если переписать разложения числа по степеням по массиву,
   где 1-ый элемент - множитель первой степени 10
   n-ый элемент - множитель n-ой степени 10

   т.е например число 8809 выглядело бы как
   {9,0,8,8}

   */
    private static LinkedList<Integer> spreadCapabilityByPower10(LinkedList<Integer> stack, int capability) {
        List<Integer> changeableStack = new ArrayList<Integer>(stack);
        int initialCapability = capability;
        LinkedList<Integer> result = new LinkedList<Integer>();
        for(int i=changeableStack.size()-1; i>=0; i--){
            int nextChangeable = changeableStack.get(i);  //Берем первое слагаемое
            int countZero = (int)Math.log10(nextChangeable); //счиатем его степень 10
            int nextPower = 1;
            if (i!=0) {
              int nextNextChangeable = changeableStack.get(i-1);
              int nextCountZero = (int)Math.log10(nextNextChangeable);
              nextPower = nextCountZero - countZero;
            } else {
              nextPower = 1;
            }
            int neededResult = (int)Math.pow(10, countZero+nextPower); //Считаем число до которого нам нужно добить
            /* Это число - первое из следующей степени
            перед этим все так сложно, чтобы учесть вариант, когда стек выглядит например так {8000, 800, 9}
            и нужно чтобы учесть разницу между 800 и 9
             */
            int difference = neededResult - nextChangeable; //Считаем чем мы будем это число добивать
            int capabilityRest = capability - difference;
            if (capabilityRest > 0 && countZero >= 0) {
                capability = capabilityRest;
                LinkedList<Integer> differenceStack = new LinkedList<Integer>();
                buildPower10Stack(difference, differenceStack); //разлагаем недостающзее число по степеням 10, ими и будем добивать
                while (differenceStack.size() > 0) {
                  result.addFirst(differenceStack.removeLast());
                }
                if (i>0) {
                    int next = changeableStack.get(i-1);
                    int nextCountZero = (int)Math.log10(next);
                    changeableStack.set(i-1, next+(int) Math.pow(10, nextCountZero));
                }
            } else {
                LinkedList<Integer> rest = new LinkedList<Integer>();
                buildPower10Stack(capability, rest);
                for (Integer restValue: rest) {
                    result.addFirst(restValue);
                }
                capability = capabilityRest;
                break;
            }
        }
        /* check is capability empty?
        * Если что-то осталось - распилить на степени 10 и добавить
        * */
         if (capability>0 && capability!=initialCapability) {
           int lastPower = (int)Math.log10(result.getFirst());
           int capabilityPower = (int)Math.log10(capability);
             for(int power=lastPower+1;power<capabilityPower; power++) {
               int needed = 9*((int)Math.pow(10, power));
               capability = capability - needed;
               result.addFirst(needed);
             }
             LinkedList<Integer> rest = new LinkedList<Integer>();
             buildPower10Stack(capability, rest);
             for (Integer restValue: rest) {
               result.addFirst(restValue);
             }
        }
        int summ = 0;
        for (Integer x: result) {
          summ+=x;
        }
        if (summ!=initialCapability) throw new IllegalArgumentException("Bad capability spread: "+initialCapability);
        return result;
    }

    public static String filter(String mask) {
        if (mask == null) return null;
        return mask.replaceAll("[^0-9"+Mask.WILDCARD+"]", "");
    }
}
