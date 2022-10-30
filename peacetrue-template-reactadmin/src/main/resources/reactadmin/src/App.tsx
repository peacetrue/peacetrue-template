import * as React from 'react';
import {Admin} from "react-admin";
import {dataProvider} from "./DataProviderInstance";
import {i18nProvider} from "./i18n";
import {resources} from "./modules";

function App() {
  return (
    <Admin
      dataProvider={dataProvider}
      i18nProvider={i18nProvider}
    >
      {resources}
    </Admin>
  );
}

export default App;
