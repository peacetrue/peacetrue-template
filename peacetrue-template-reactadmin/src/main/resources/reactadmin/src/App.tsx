import * as React from 'react';
import {Admin, Resource, ListGuesser} from "react-admin";
import jsonServerProvider from 'ra-data-json-server';

const dataProvider = jsonServerProvider(process.env.REACT_APP_BASE_URL);

function App() {
  return (
    <Admin dataProvider={dataProvider}>
      <Resource name="users" list={ListGuesser}/>
    </Admin>
  );
}

export default App;
