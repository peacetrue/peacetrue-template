import {mergeTranslations} from "react-admin";
import {RaMessages} from "./RaMessages_zh";
import polyglotI18nProvider from "ra-i18n-polyglot";
import {getModules} from "../modules";

// 动态导出消息
export const messages = getModules().map(item => item.messages);

// 参考 https://marmelab.com/react-admin/TranslationSetup.html，当前只支持中文
export const i18nProvider = polyglotI18nProvider(
  (locale: string) => mergeTranslations(RaMessages, ...messages),
  'cn',
  [{locale: 'cn', name: 'Chinese'},],
  {allowMissing: true}
);
