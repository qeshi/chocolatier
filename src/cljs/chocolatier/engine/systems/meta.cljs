(ns chocolatier.engine.systems.meta
  "System for handling changes to the game state
   i.e add/remove entities, changing scenes, etc."
  (:require [chocolatier.engine.events :as ev]
            [chocolatier.engine.core :refer [mk-state]]))


(defn meta-system
  "Processes all events on the :meta queue and returns an updated game
   state. This is used for adding, removing entities or any other game
   state modifications. See core/mk-state for all available options"
  [state]
  (let [queue (get-in state ev/queue-path)
        events (set (ev/get-events queue [:meta]))]
    (reduce #(mk-state %1 (:msg %2)) state events)))
