// @flow

import React from "react";
import { render } from "react-dom";
import Home from "./Home";
import 'bootstrap/dist/css/bootstrap.min.css';
const demoNode = document.querySelector("#demo");

if (demoNode) {
  render(<Home />, demoNode);
}
