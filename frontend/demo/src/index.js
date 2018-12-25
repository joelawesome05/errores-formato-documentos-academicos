// @flow

import React from "react";
import { render } from "react-dom";
import Home from "./Home";

const demoNode = document.querySelector("#demo");

if (demoNode) {
  render(<Home />, demoNode);
}
