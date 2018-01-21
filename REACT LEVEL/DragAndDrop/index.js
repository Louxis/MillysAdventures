import React from 'react-native';
import Viewport from './Screen';
import ScoreScreen from './ScoreScreen'
import { StackNavigator } from 'react-navigation';
import getSlideFromRightTransition from 'react-navigation-slide-from-right-transition';
 


import { DrawerNavigator } from "react-navigation";
const HomeScreenRouter = DrawerNavigator(
  {
    Home: { screen: Viewport },
    Score: { screen: ScoreScreen },
}, {
  transitionConfig: getSlideFromRightTransition
});

React.AppRegistry.registerComponent('DragAndDrop', () => HomeScreenRouter);