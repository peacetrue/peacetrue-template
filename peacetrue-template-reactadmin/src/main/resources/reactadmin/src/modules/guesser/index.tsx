import {ListGuesser, Resource} from "react-admin";
import {Module} from "../Module";

export default {
  resource: <Resource key="users" name="users" list={ListGuesser}/>,
  messages: {}
} as Module;
