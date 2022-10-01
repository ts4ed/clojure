(defn euclidian-division
  [х у]
  [(quot х у) (rem х у)])
(euclidian-division 42 8)
;= [5 2]

(let [[q r] (euclidian-division 59 7)]
  (str "59/7 = " q " * 7 + " r))
