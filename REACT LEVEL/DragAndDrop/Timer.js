import React, {Component} from 'react';
import { AppRegistry, Image, StyleSheet, View, Text } from "react-native";
import TimerCountdown from 'react-native-timer-countdown'

export default class Timer extends Component {
    render() {
        return (
            <TimerCountdown
                initialSecondsRemaining={40}
                onTick={() => {this.updateText}}
                onTimeElapsed={() => console.log('complete')}
                allowFontScaling={true}
                style={{ fontSize: 20 }}
            />
        )
    }
}