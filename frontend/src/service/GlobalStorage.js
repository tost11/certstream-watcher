import { useState, useEffect } from 'react';
import * as RTStorage from 'rt-storage';

export default function useGlobalStorage(storageOptions) {
    const storage = new RTStorage(storageOptions);
    const useStorage = (key, initialData) => {
        const [data, setState] = useState(initialData);    useEffect(() => {
            function handleStorageChange(data) {
                setState(data);
            }
            storage.getItem(key).then(lastData => {
                if (lastData) {
                    setState(lastData);
                }
            });
            const subscription = storage.subscribe(key, handleStorageChange);
            return () => {
                subscription.unsubscribe();
            };
        }, []);    const setData = async(newData) => {
            let newValue;
            if (typeof newData === 'function') {
                newValue = newData(data);
            } else {
                newValue = newData
            }
            setState(newValue);
            await storage.setItem(key, newValue);
        };

        return [data, setData];
    };
    return useStorage;
};