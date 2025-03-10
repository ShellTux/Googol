#!/bin/sh
set -xe

SESSION_NAME="googol-tmux"
target_jar=./target/googol-1.0-SNAPSHOT.jar
barrels_command="java -cp $target_jar com.googol.IndexStorageBarrel"
gateway_command="java -cp $target_jar com.googol.Gateway"
downloaders_command="java -cp $target_jar com.googol.Downloaders"
queue_command="java -cp $target_jar com.googol.UrlQueue"

barrels_pane="$SESSION_NAME:1.1"
gateway_pane="$SESSION_NAME:1.3"
downloaders_pane="$SESSION_NAME:1.2"
queue_pane="$SESSION_NAME:1.4"

start_barrel() {
  tmux send-keys -t "$barrels_pane" "$barrels_command" C-m
}

start_gateway() {
  tmux send-keys -t "$gateway_pane" "$gateway_command" C-m
}

start_downloader() {
  tmux send-keys -t "$downloaders_pane" "$downloaders_command" C-m
}

start_queue() {
  tmux send-keys -t "$queue_pane" "$queue_command" C-m
}

set -xe

tmux new-session -d -s "$SESSION_NAME" -c "$(dirname "$0")"

sleep 3

tmux split-window -v -t "$SESSION_NAME:1"
tmux split-window -h -t "$SESSION_NAME:1.1"
tmux split-window -h -t "$SESSION_NAME:1.3"

sleep 1

for start_function in \
  start_barrel \
  start_gateway \
  start_downloader \
  start_queue
do
  (
    set -x
    $start_function
    sleep 2
  )
done
