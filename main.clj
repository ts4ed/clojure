(def x 1)
(def х "hello")

(def m {:a 5 :b 6
        :c [7 8 9]
        :d {:e 10 :f 11}
        "foo" 88
        42 false})

(let [{a :a b :b {e :e} :d} m]
          (+ (* 2 e) a b))

(def brian {"name" "Brian" "age" 31 "location" "British Columbia"}) 
  (let [{:strs [name age location]} brian]
  (format "%s is %s years old and lives in %s." name age location))

(def christophe {'name "Christophe" 'age 33 'location "Rhone-Alpes"})
  (let [{:syms [name age location]} christophe]
  (format "%s is %s years old and lives in %s." name age location))


;Вариант 1
(def strange-adder (fn adder-self-reference
                      ([x] (adder-self-reference x 1))
                      ([x y] (+ x y))))

    (strange-adder 10)
    (strange-adder 10 50)
;Вариант 2
(defn strange-adder
    ([x] (strange-adder x 1))
    ([x y] (+ x y)))

;Вариант 1
(def redundant-adder (fn redundant-adder
                      [x у z]
                      (+ x у z)))

;Вариан 2
(defn redundant-adder
        [x у z]
        (+ x у z))


(defn make-user
[& [user-id]]
{:user-id (or user-id
(str (java.util.UUID/randomUUID)))})



(defn make-user
  [username & {:keys [email join-date] 
        :or {join-date (java.util.Date.)}}] 
{:username username
:join-date join-date
:email email
:exp-date (java.util.Date, (long (+ 2.592e9 (.getTime join-date))))})
;; 2.592e9 -> один месяц в мсек


(make-user "Bobby") 
:= {:username "Bobby", :join-date #<Date Mon Jan 09 16:56:16 EST 2012>,
; = :email nil, :exp-date #<Date Wed Feb 08 16:56:16 EST 2012>}


(defn foo
[& {k ["m" 9]}]
(inc k))

(foo ["m" 9] 19)


#(do (printIn (str %1 \^ %2))
(Math/pow %1 %2))

(fn [х у]
(printIn (str х \^ У))
(Math/pow х у))


(loop [х 5] 
    (if (neg? х)
        x
        (recur (dec x))))

(loop [x 10]
        (if (neg? x)
        x
        (recur (dec x))))

(defn countdown
[х]
(if (zero? x)
:blastoff!
(do (println x)
(recur (dec x))))) 




; Простейшая реализация Clojure REPL
(defn embedded-repl
"A naive Clojure REPL implementation. Enter ':quit'
to exit."
[]
(print (str (ns-name *ns*) " >>> "))
(flush)
(let [expr (read)
value (eval expr)]
(when (not= :quit value)
(println value)
(recur))))


; Пример
 (defn average2
 [numbers]
 (/ (apply + numbers) (count numbers))) 
 (average2 [3 7 5])

clojure.main


(defn call-twice [f x]
(f x)
(f x))
(call-twice println 555)


; Вариант 1
( reduce
(fn [m v] 
(assoc m v (* v v)))
{} 
[ 1 2 3 4])

; Вариант 2
( reduce
#( assoc % %2 (* %2 %2))
{}
[1 2 3 4 ])



(def args [2 -2 10])
(apply * 0.5 3 args) 
;-60.0


; Вариант 1
(def only-strings (partial filter string?))
(only-strings ["a" 5 "b" 6])
;("a" "b")

; Вариант 2
(#(filter string? %) ["а" 5 "b" 6])
;("а" "b")


(#(filter % ["а" 5 "Ь" 6]) string?)
;= ("а" "Ь”)
(#(filter % ["а" 5 "Ь" 6]) number?)
;= (5 6)


; Вариант 1
(#(apply map * %&) [1 2 3] [4 5 6] [7 8 9]) 
;= (28 80 162)

; Вариант 2
((partial map *) [1 2 3] [4 5 6] [7 8 9]) 
;= (28 80 162)

; Композиция функций. числа сумируются, в отрицание, в строку
(def negated-sum-str (comp str - +)) 
(negated-sum-str 10 12 3.4)
;= "-25.4"



; Вызываем загрузку пространства имен и присваиваем префикс
(require '[clojure.string :as str]) 

; Передаваемые строки буду изменены с CamelCase на lower + "-"
(def camel->keyword (comp keyword
                        str/join
                        (partial interpose \-)
                        (partial map str/lower-case)
                        #(str/split % #"(?<=[a-z])(?=[A-Z])")))
(camel->keyword "lowerCamelCase")
;= :lower-camel-case


; С использованием макросов
(defn camel->keyword
        [s]
        (->> (str/split s #"(?<=[a-z])(?=[A-Z])")
                (map str/lower-case)
                (interpose \-)
                str/join
                keyword))


; вложение в функцию предыдущей функции
(def camel-pairs->map (comp (partial apply hash-map)
                                (partial map-indexed (fn [i x]
                                                        (if (odd? i)
                                                        x
                                                        (camel->keyword x ))))))

(camel-pairs->map ["CamelCase" 5 "lowerCamelCase" 3])


(defn print-logger
        [writer] 
        #(binding [*out* writer] 
                (println %)))


(require 'clojure.java.io)
(defn file-logger
        [file] 
        #(with-open [f (clojure.java.io/writer file :append true)] 
                ((print-logger f) %))) 
(def log->file (file-logger "messages.log"))
(log->file "hello")


(defn multi-logger
        [& logger-fns] 
        #(doseq [f logger-fns] 
                (f %)))

(def log (multi-logger 
        (print-logger *out*)
        (file-logger "messages.log")))
(log "hello again")


(defn timestamped-logger
        [logger]
        #(logger (format "[%1$tY-%1$tm-%1$te %1$tH:%1$tM:%1$tS] %2$s"
                        (java.util.Date.) %))) 

(def log-timestamped (timestamped-logger
                        (multi-logger
                        (print-logger *out*)
                        (file-logger "messages.log"))))
(log-timestamped "goodbye, now")


; Функция которая принимает имана пользователей и возвращает подписчиков
(require 'clojure.xml)
(defn twitter-followers
        [username]
        (->> (str "https://api.twitter.com/1/users/show.xml?screen_name="
                username)
                clojure.xml/parse
                :content
                (filter (comp #{:followers_count} :tag))
                first
                :content
                first
                Integer/parseInt))

(twitter-followers "ClojureBook")

(defn prime?
        [n]
        (cond
        (== 1 n) false
        (== 2 n) true
        (even? n) false
        :else ( ->> (range 3 (inc (Math/sqrt n)) 2)
                (filter #(zero? (rem n %)))
                empty?)))
; Обычное вычисление
(time (prime? 1125899906842679)) 

; Мемоизация функции
(let [m-prime? (memoize prime?)]
        (time (m-prime? 1125899906842679))
        (time (m-prime? 1125899906842679)))

        (def m {:a 5 :b 6})


(defn swap-pairs
[sequential]
(into (empty sequential)
(interleave
(take-nth 2 (drop 1 sequential))
(take-nth 2 sequential))))


(defn map-map
[f m]
(into (empty m)
(for [[k v] m] 
[k (f v)])))

(map-map inc (hash-map :z 5 :с 6 :а 0))
;= {:z 6, :а 1, :с 7}
(map-map inc (sorted-map :z 5 :с 6 :а 0))
; = {:а 1, :с 7, :z 6}

(doseq [x (range 3)]
(println x))



(let [r (range 3)
rst (rest r)]
(prn (map str rst))
(prn (map #(+ 100 %) r))
(prn (conj r -1) (conj rst 42)))

; Сравнение обхода последовательности 
(let [s (range 1е6)] 
(time (count s)))
; И обхода списка
(let [s (apply list (range 1e6))] 
(time (count s)))


(defn random-ints
"Возвращает ленивую последовательность случайных целых чисел в диапазоне
[О,limit)."
[limit]
(lazy-seq
(cons (rand-int limit) 
(random-ints limit)))) 
(take 10 (random-ints 50))


(defn random-ints
[limit]
(lazy-seq
(println "realizing random number") 
(cons (rand-int limit)
(random-ints limit))))
(def rands (take 10 (random-ints 50))) 

(def sm (sorted-map :z 5 :x 9 :y 0 :b 2 :a 3 :c 4))

sm
(rseq sm)
(subseq sm <= :c)
(subseq sm > :b <= :y) 

(sort > (repeatedly 10 #(rand-int 100)))
(sort-by first < (map-indexed vector "Clojure"))



(defn magnitude
        [х]
        (-> х Math/log10 Math/floor)) 

(defn compare-magnitude
        [a b]
        (- (magnitude a) (magnitude b)))


(sorted-set-by compare-magnitude 10 1000 500)
;= #{10 500 1000}


; Теперь только действительно равные значения 

(defn compare-magnitude
        [a b]
        (let [ diff (- (magnitude a) (magnitude b))]
                ( if (zero? diff)
                        (compare a b)
                        diff)))




; "Принимает коллекцию координат точек (в виде кортежей [х у]) и возвращает Функцию, обеспечивающую линейную интерполяцию между этими точками."
(defn interpolate
        [points]
                (let [results (into (sorted-map) (map vec points))] 
                        (fn [x]
 (let [[xa ya] ( fir s t (rsubseq results <= x)) 
[xb yb] ( fir s t (subseq results > x))]
( i f (and xa xb) 
(/ (+ (* ya (- xb x)) (* yb (- x xa))) 
(- xb xa))
(or ya yb))))))

