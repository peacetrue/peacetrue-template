import {mergeTranslations} from "react-admin";
import {RaMessages} from "./RaMessages_zh";
import {ModuleMessages} from "./ModuleMessages_zh";
import polyglotI18nProvider from "ra-i18n-polyglot";

export const getMessages = (locale: string) => mergeTranslations(RaMessages, ...ModuleMessages);
// 参考 https://marmelab.com/react-admin/TranslationSetup.html，当前只支持中文
export const i18nProvider = polyglotI18nProvider(getMessages, 'cn', [{
  locale: 'cn',
  name: 'Chinese'
},], {allowMissing: true});
