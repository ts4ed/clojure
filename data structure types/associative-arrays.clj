{:а 5 :b 6}
; = {: а 5, : b 6}
{:а 5 :а 5}
; = #<IllegalArgumentException java.lang.IllegalArgumentException:
;= Duplicate key: :a>

(hash-map :a 5 :b 6)
;= {:a 5, :b 6}
(apply hash-map [:a 5 :b 6])
;= {:a 5, :b 6}
(keys m)
;= (:а :b :с)
(vals m)
;= (1 2 3)

; Вариант 2

(map key m)
:= (:а :с :b)
(map val m)
:= (1 3 2)

(def playlist
  [{:title "Elephant", :artist "The White Stripes", :year 2003}
   {:title "Helioself", :artist "Papas Fritas", :year 1997}
   {:title "Stories from the City, Stories from the Sea",
    :artist "PJ Harvey", :year 2000}
   {:title "Buildings and Grounds", :artist "Papas Fritas", :year 2000}
   {:title "Zen Rodeo", :artist "Mardi Gras BB", :year 2002}])

(map :title playlist)
;= ("Elephant" "Helioself" "Stories from the City, Stories from the Sea"
;= "Buildings and Grounds" "Zen Rodeo")

; Деструктуризация ассоциативных массивов
(defn summarize [{:keys [title artist year]}]
  (str title " / " artist " / " year))

(group-by :artist playlist)

(group-by #(rem % 5) (range 10))
;= {0 [0 3 6 9], 1 [1 4 7], 2 [2 5 8]}

(into {} (for [[k v] (group-by key-fn coll)]
           [k (summarize v)]))

;Пример взаимодействия функций
(defn reduce-by
  [key-fn f init coll]
  (reduce (fn [summaries x]
            (let [k (key-fn x)]
              (assoc summaries k (f (summaries k init) x))))
          {} coll))

(def orders
  [{:product "Clock", :customer "Wile Coyote", :qty 6, :total 300}
   {:product "Dynamite", :customer "Wile Coyote", :qty 20, :total 5000}
   {:product "Shotgun", :customer "Elmer Fudd", :qty 2, :total 800}
   {:product "Shells", :customer "Elmer Fudd", :qty 4, :total 100}
   {:product "Hole", :customer "Wile Coyote", :qty 1, :total 1000}
   {:product "Anvil", :customer "Elmer Fudd", :qty 2, :total 300}
   {:product "Anvil", :customer "Wile Coyote", :qty 6, :total 900}])

(reduce-by :customer #(+ %1 (:total %2)) 0 orders)
; = {"Elmer Fudd" 1200, "Wile Coyote" 7200}

(reduce-by :product #(conj %1 (:customer %2)) #{} orders)
;= {"Anvil" #{"Wile Coyote" "Elmer Fudd"},
;= "Hole” #{"Wile Coyote"},
;= "Shells" #{"Elmer Fudd"},
;= "Shotgun" #{"Elmer Fudd”},
;= "Dynamite" #{"Wile Coyote"},
;= "Clock" #{"Wile Coyote"}}

; Варианты доступа к духуровневой информации
;1
(fn [order]
  [(:customer order) (:product order)])
;2
#(vector (:customer %) (:product %))
;3
(fn [{:keys [customer product]}]
  [customer product])
;4
(juxt :customer :product)

(reduce-by (juxt :customer :product)
           #(+ %1 (:total %2)) 0 orders)
;= {["Wile Coyote" "Anvil"] 900,
;= ["Elmer Fudd" "Anvil"] 300,
;= ["Wile Coyote" "Hole"] 1000,
;= ["Elmer Fudd" "Shells"] 100,
;= ["Elmer Fudd" "Shotgun"] 800,
;= ["Wile Coyote" "Dynamite"] 5000,
;= ["Wile Coyote" "Clock"] 300}

; Изменяем reduce-by для обработки вложенных массивов 
(defn reduce-by-in
  [keys-fn f init coll]
  (reduce (fn [summaries x]
            (let [ks (keys-fn x)]
              (assoc-in summaries ks
                        (f (get-in summaries ks init) x))))
          {} coll))

(reduce-by-in (juxt :customer :product)
              #(+ %1 (:total %2)) 0 orders)
;= {"Elmer Fudd" {"Anvil" 300,
;=                "Shells" 100,
;=                "Shotgun" 800},
;= "Wile Coyote” {"Anvil" 900,
;=                "Hole" 1000,
;=                "Dynamite" 5000,
;=                "Clock" 300}}

(def flat-breakup
  {["Wile Coyote" "Anvil"] 900,
   ["Elmer Fudd" "Anvil"] 300,
   ["Wile Coyote" "Hole"] 1000,
   ["Elmer Fudd" "Shells"] 100,
   ["Elmer Fudd" "Shotgun"] 800,
   ["Wile Coyote" "Dynamite"] 5000,
   ["Wile Coyote" "Clock"] 300})

(reduce #(apply assoc-in %1 %2) {} flat-breakup)
;= {"Elmer Fudd" {"Shells" 100,
;=                "Anvil" 300,
;=                "Shotgun" 800},
;= "Wile Coyote" {"Hole" 1000,
;=                "Dynamite" 5000,
;=                "Clock” 300,
;=                "Anvil" 900}}

(def a {:a 5 :Ь 6 :с 7 :d 8})
(def b (assoc a :с 0))

(def version1 {:name "Chas" :info {:age 31}})
;= #' user/version1
(def version2 (update-in version1 [:info :age] + 3))
;= #'user/version2
versionl
;= {:info {:age 31}, :name "Chas"}
version2
;= {:info {:age 34}, :name "Chas"}

(defn transient-capable?
  "Возвращает true, если для данной коллекции можно получить
переходный вариант. То есть, проверяет - преуспеет ли
'(transient coll) ’ ."
  [coll]
  (instance? clojure.lang.IEditableCollection coll))

; Cоздание метаданных
(def a ^{:created (System/currentTimeMillis)}
  [1 2 3])
(meta a)
;= {:created 1322065198169}
(meta ^:private [1 2 3])
;= {:private true}
(meta ^:private ^:dynamic [1 2 3])
;= {:dynamic true, :private true}

; Замещение метаданных
(def b (with-meta a (assoc (meta a)
                           :modified (System/currentTimeMillis))))
(meta b)
;= {:modified 1322065210115, :created 1322065198169}

; Обновление метаданных 
(def b (vary-meta a assoc :modified (System/currentTimeMillis)))
(meta b)
;= {:modified 1322065229972, :created 1322065198169}

(= ^{:а 5} 'any-value
   ^{:b 5} 'any-value)
;= true

(defn populate
  "Включает значение :оп в ячейках, определяемых координатами [у, х]."
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board
          living-cells))

; "Создает прямоугольное игровое поле с указанной шириной и высотой."
(defn empty-board
  [w h]
  (vec (repeat w (vec (repeat h nil)))))

;  "Включает значение :оп в ячейках, определяемых координатами [у, х]."
(defn populate
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board
          living-cells))

(def glider (populate (empty-board 6 6) #{[2 0] [2 1] [2 2] [1 2] [0 1]}))

; [[nil :on nil nil nil nil]
;  [nil nil :on nil nil nil]
;  [:on :on :on nil nil nil]
;  [nil nil nil nil nil nil]
;  [nil nil nil nil nil nil]
;  [nil nil nil nil nil nil]]

(defn neighbours
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

(defn count-neighbours
  [board loc]
  (count (filter #(get-in board %) (neighbours loc))))

;"Возвращает следующее состояние игрового поля, используя индексы для
; определения координат ячеек, соседних с живыми клетками."

(defn indexed-step
  [board]
  (let [w (count board)
        h (count (first board))]
    (loop [new-board board x 0 y 0]
      (cond
        (>= x w) new-board
        (>= y h) (recur new-board (inc x) 0)
        :else
        (let [new-liveness
              (case (count-neighbours board [x y])
                2 (get-in board [x y])
                3 :on
                nil)]
          (recur (assoc-in new-board [x y] new-liveness) x (inc y)))))))
(-> (iterate indexed-step glider) (nth 8) pprint)

(defn indexed-step3
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce
     (fn [new-board [x y]]
       (let [new-liveness
             (case (count-neighbours board [x y])
               2 (get-in board [x y])
               3 :on
               nil)]
         (assoc-in new-board [x y] new-liveness)))
     board (for [x (range h) y (range w)] [x y]))))

(partition 3 1 (range 5))
(partition 3 1 (concat [nil] (range 5) [nil]))

; "Возвращает ленивую последовательность окон с тремя элементами в каждом,
; центрами в которых является элемент coll. "
(defn window

  [coll]
  (partition 3 1 (concat [nil] coll [nil])))

; "Создает последовательность окон 3x3 на основе тройки из трех
; последовательностей."
(defn cell-block
  [[left mid right]]
  (window (map vector
               (or left (repeat nil)) mid (or right (repeat nil)))))

;Возвращает ленивую последовательность окон с тремя элементами в каждом,
;центрами в которых является элемент coll, и дополненную значением
;pad или nil, если необходимо."
(defn window
  ([coll] (window nil coll))
  ([pad coll]
   (partition 3 1 (concat [pad] coll [pad]))))

;"Создает последовательности окон 3x3 из троек последовательностей по 3
; элемента в каждой."
(defn cell-block

  [[left mid right]]
  (window (map vector left mid right)))

;"Возвращает признак наличия живой клетки ( n il или :оп) в центральной
; ячейке для выполнения следующего шага."
(defn liveness
  [block]
  (let [[_ [_ center _] _] block]
    (case (- (count (filter #{:on} (apply concat block)))
             (if (= :on center) 1 0))
      2 center
      3 :on
      nil)))

;"Возвращает следующее состояние центра строки."

(defn- step-row
  [rows-triple]
  (vec (map liveness (cell-block rows-triple))))

;"Возвращает следующее состояние игрового поля."

(defn index-free-step
  [board]
  (vec (map step-row (window (repeat nil) board))))

(= (nth (iterate indexed-step glider) 8)
   (nth (iterate index-free-step glider) 8))

; В итоге легкое решение 

(defn neighbours
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))

; "Возвращает следующее состояние мира"
(defn step
  [cells]
  (set (for [[loc n] (frequencies (mapcat neighbours cells))
             :when (or (= n 3) (and (= n 2) (cells loc)))]
         loc)))

(->> (iterate step #{[2 0] [2 1] [2 2] [1 2] [0 1]})
     (drop 8)
     first
     (populate (empty-board 6 6))
     pprint)

; Продолжение

;"Возвращает функцию step для реализации клеточного автомата,
;neighbours принимает координаты и возвращает упорядоченную коллекцию
;координат. Функции survive? и birth? - это предикаты, проверяющие
;число живых соседей."

(defn stepper
  [neighbours birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))
