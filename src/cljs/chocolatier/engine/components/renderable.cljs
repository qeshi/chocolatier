(ns chocolatier.engine.components.renderable
  (:require [chocolatier.engine.ecs :as ecs]
            [chocolatier.engine.events :as ev]
            [chocolatier.engine.pixi :as pixi]))


(defn cleanup-sprite-state
  "Removes sprite from the stage belonging to the entity and returns state"
  [state entity-id]
  (let [stage (-> state :game :rendering-engine :stage)
        {:keys [sprite] :as cs} (ecs/get-component-state state :sprite entity-id)]
    (pixi/remove-child! stage sprite)
    state))

(defn cleanup-text-state
  "Removes sprite from the stage belonging to the entity and returns state"
  [state entity-id]
  (let [stage (-> state :game :rendering-engine :stage)
        {:keys [text-obj]} (ecs/get-component-state state :text-sprite entity-id)]
    (pixi/remove-child! stage text-obj)
    state))

(defn set-position!
  "Update the screen x, y position of the sprite based on any move events
   from a component inbox. Returns the updated sprite."
  [sprite position]
  (let [{:keys [screen-x screen-y screen-z]} position]
    ;; Mutate the x and y position of the sprite if there was any
    ;; move changes
    (aset sprite "position" (js-obj "x" screen-x "y" screen-y "z" screen-z))))

;; TODO figure out a way to not need the stage so we can more easily
;; create sprite state. For example, in the attack component we must
;; rely on a sprite-fn instead of constructing the sprite state inside
;; of the attack component fn
(defn mk-sprite-state
  "Returns a hashmap of render component state. Optionally pass in the
   sprite frame as the last argument to render to the position right away"
  ([stage loader img-path]
   {:sprite (pixi/mk-sprite-from-cache! stage loader img-path)})
  ([stage loader img-path frame z-index]
   {:sprite (pixi/mk-sprite-from-cache! stage loader img-path frame z-index)}))

(defn mk-text-sprite-state
  [stage text styles]
  {:text-obj (pixi/mk-text! stage text (clj->js styles))})

(defn render-sprite
  "Renders the sprite in relation to the position of the entity and
   frame of the spritesheet deterimined by the animateable state"
  [entity-id component-state {:keys [position animateable]}]
  (let [sprite (:sprite component-state)
        frame (:frame animateable)]
    ;; Side effects!
    (set-position! sprite position)
    ;; If there is an animation frame then update the spritesheet frame
    (when frame (pixi/set-sprite-frame! sprite frame))
    component-state))

(defn render-text
  "Renders text in relation to the position of the entity"
  [entity-id component-state context]
  (let [{text-state :text position :position} context
        {:keys [text rotation]} text-state
        text-obj (:text-obj component-state)]
    ;; Mutate the text object position and text
    (set-position! text-obj position)
    (when text
      (aset text-obj "text" text)
      (aset text-obj "rotation" rotation))
    component-state))
