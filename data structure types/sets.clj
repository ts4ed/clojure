#{1 2 3}
:= #{1 2 3}
#{1 2 3 3}
; = #<IllegalArgumentException java.lang.IllegalArgumentException:
;= Duplicate key: 3>

(hash-set :а :b :с :d)

; Множество из коллекции

(set [1 6 1 8 3 7 7])

(apply str (remove (set "aeiouy") "vowels are useless"))
;= "vwls r slss"

(defn numeric? [s] (every? (set "0123456789") s))
;= #'user/numeric?
(numeric? "123")
;= true
(numeric? "42b")
:= false