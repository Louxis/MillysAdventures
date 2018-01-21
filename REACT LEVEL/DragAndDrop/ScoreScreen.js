import React, { Component } from "react";
import { AppRegistry, Image, StyleSheet, View, Text, Animated } from "react-native";
import score from "./global";
import PopupDialog, { SlideAnimation } from 'react-native-popup-dialog';

const slideAnimation = new SlideAnimation({
  slideFrom: 'bottom',
});

export default class ScoreScreen extends Component { 
  render() {
    return(
      <View style={{flex: 1}}>
        <PopupDialog
          ref={(popupDialog) => { this.popupDialog = popupDialog; }}
          dialogAnimation={slideAnimation}
        >
          <View>
            <Text>Hello</Text>
          </View>
        </PopupDialog>
      </View>
  )
  }
}