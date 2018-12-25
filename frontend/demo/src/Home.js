import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import FileInput from './FileInput';
import App from './App';

class Home extends Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} component={FileInput} />
                    <Route path='/verResultados/:name' exact={true} component={App} />
                </Switch>
            </Router>
        );
    }
}

export default Home;
