import React, { Component } from "react";
import { AppRegistry, Image, StyleSheet, View, Text, Animated, Button, Icon, TouchableHighlight } from "react-native";
import Draggable from "./Draggable";
import nuts from "./global";
import PopupDialog, { SlideAnimation, DialogButton } from 'react-native-popup-dialog';
import RNRestart from 'react-native-restart';
import CarrotContainer from './CarrotContainer'



const timer = require('react-native-timer');

const slideAnimation = new SlideAnimation({
  slideFrom: 'bottom'
});



export default class Screen extends Component {
  constructor() {
    super() 
    this.state = {
       time: 40,
       imgVisible : false
    }
    timer.setInterval(
      this, 'test', this.updateText , 1000
    );
 }
 
 updateText = () => {
   if (this.state.time === 0 || nuts.nuts === 0){   
    timer.clearInterval(this);
    //this.props.navigation.navigate('Score');
    this.popupDialog.show() 

   } else{
    this.setState({time: this.state.time - 1})
   }   
   const remainingTime = this.state.time;
    if(remainingTime < 9){
      nuts.score = 1;
    }
    if(remainingTime >= 9 && remainingTime <= 20){
      nuts.score = 2;
    }
 }

 restart = () => {
  RNRestart.Restart();
 }

  render() {
    const resizeMode = 'stretch';
    return (        
      <View style={styles.mainContainer}>                           
        <View
          style={styles.backView}>
          <Image
            style={styles.backImage}
            source={require('./img/background.png')}
          />
        </View>         
        <View style={{top: 10, left: 10 }}>
          <Text style={styles.timerText}>
            {this.state.time}
          </Text>
        </View>
        <View style={{
        flex: 1,
        flexDirection: 'column',
        justifyContent: 'space-between',
        }}>
          <View style={{flex: 1, flexDirection: 'row', top: 150}}>            
            <Draggable />
            <Draggable />
            <Draggable />
          </View>
          <View style={{flex: 1, flexDirection: 'row', top: 75, left: 80}}>
            <Draggable />
            <Draggable />
          </View>
        </View>
      <PopupDialog        
        dismissOnTouchOutside={false}
        width={300}
        ref={(popupDialog) => { this.popupDialog = popupDialog; }}
          dialogAnimation={slideAnimation}
        >
      <View
          style={styles.backView}>
          
          <Image
            style={styles.backImage}            
            source={require('./img/newframe.png')}
          />
        </View> 
      <View style={{flex: 1, top: 5, padding: 30, justifyContent: 'center',
        alignItems: 'center'}}> 
          <CarrotContainer />
        <TouchableHighlight onPress={this.restart}>        
          <Image
          style={styles.button}
          style={{width: 95, height: 95, resizeMode: 'contain'}}
          source={require('./img/Play_button.png')}
          />             
        </TouchableHighlight>
      </View>  
      </PopupDialog>   
      </View>
    );
  }
}

const styles = StyleSheet.create({
  mainContainer: {
    flex: 1
  },
  ballContainer: {
    height:200
  },
  row: {
    flexDirection: "row"
  },  
  dropZone: {
    height: 200,
    backgroundColor: "#00334d"
  },
  text: {
    marginTop: 25,
    marginLeft: 5,
    marginRight: 5,
    textAlign: "center",
    color: "#fff",
    fontSize: 25,
    fontWeight: "bold"
  },
  timerText: {
    color: 'orange', 
    fontFamily: 'Roboto', 
    fontWeight: 'bold', 
    fontSize: 30
  },
  backView: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    width: '100%',
    height: '100%',
  },
  backImage: {
    flex: 1,
    top: 0,
    resizeMode: "stretch",
    width:'100%'              
  }
});