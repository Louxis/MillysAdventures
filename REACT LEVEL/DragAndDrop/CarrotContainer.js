import React, { Component } from "react";
import { AppRegistry, Image, StyleSheet, View, Text, Animated, Button, Icon, TouchableHighlight } from "react-native";
import nuts from './global'

export default class CarrotContainer extends Component {
    render(){
        if(nuts.score == 1){
            return(
                <View style={{flex: 1, flexDirection: 'row'}}>          
                    <Image style={{width: 90, height: 90, zIndex: 3, resizeMode: 'contain'}} source={require('./img/Carrot.png')} />
                  </View>)
        }
        if(nuts.score == 2){
            return(
                <View style={{flex: 1, flexDirection: 'row'}}>          
                    <Image style={{width: 90, height: 90, zIndex: 3, resizeMode: 'contain'}} source={require('./img/Carrot.png')} />
                    <Image style={{width: 90, height: 90, zIndex: 3, resizeMode: 'contain'}} source={require('./img/Carrot.png')} />    
                  </View>)
        }
        if(nuts.score == 3){
            return(
                <View style={{flex: 1, flexDirection: 'row'}}>          
                    <Image style={{width: 90, height: 90, zIndex: 3, resizeMode: 'contain'}} source={require('./img/Carrot.png')} />
                    <Image style={{width: 90, height: 90, zIndex: 3, resizeMode: 'contain'}} source={require('./img/Carrot.png')} />         
                    <Image style={{width: 90, height: 90, zIndex: 3, resizeMode: 'contain'}} source={require('./img/Carrot.png')} />
                  </View>)
        }
    }      
}