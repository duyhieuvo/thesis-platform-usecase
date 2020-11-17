#!/usr/bin/env bash

function parseCmd () {
    local any_selected=false
    while [[ $# -gt 0 ]]; do
        case "$1" in
            pub-sub)
                any_selected=true
                PUBSUB=true
                shift
                ;;
            event-queue)
                any_selected=true
                EVENTQUEUE=true
                shift
                ;;
            command-queue)
                any_selected=true
                COMMANDQUEUE=true
                shift
                ;;
            event-replay)
                any_selected=true
                EVENTREPLAY=true
                shift
                ;;
            producer)
                PRODUCER=true
                shift
                ;;
            consumer-1)
                CONSUMER1=true
                shift
                ;;
            consumer-2)
                CONSUMER2=true
                shift
                ;;
            reader)
                READER=true
                shift
                ;;
            all)
                ALL=true
                shift
                ;;
            *)
                usage "Unknown option: $1"
                return $?
                ;;
        esac
    done
    if [ "${any_selected}" == false ]; then
        usage "Specify one use case"
        return $?
    fi
    return 0
}